# Payment Service

An event-driven payment service. A payment is accepted over REST, persisted as `PENDING`, and published to Kafka. A settlement consumer picks the event up asynchronously, marks the payment `SETTLED`, and emits a settlement event that downstream systems (ledger, notifications) can subscribe to.

This separation means the API responds immediately with `202 Accepted` while the actual settlement happens off the request thread — the pattern real payment rails use so a slow downstream never blocks the caller.

## Flow

```
POST /api/v1/payments
      │
      ▼
 store as PENDING ──► publish PaymentEvent ──► topic: payments.initiated
                                                     │
                                                     ▼
                                        PaymentInitiatedListener
                                                     │
                                             mark SETTLED
                                                     │
                                                     ▼
                                          topic: payments.settled
```

## Tech stack

| Concern       | Choice                     |
|---------------|----------------------------|
| Language      | Java 17                    |
| Framework     | Spring Boot 3.3            |
| Messaging     | Spring for Apache Kafka    |
| Serialization | JSON (Spring Kafka)        |
| Testing       | JUnit 5, Mockito, Embedded Kafka |
| Build         | Maven                      |

Payment state is held in an in-memory store to keep the project focused on the messaging flow; the `PaymentStore` interface is the seam where a JPA or JDBC repository would drop in.

## Running it

Start a local Kafka broker (KRaft mode, no ZooKeeper):

```bash
docker compose up -d
```

Then run the service:

```bash
mvn spring-boot:run
```

The API listens on port `8081`; Kafka is expected at `localhost:9092`.

## Trying it out

```bash
# initiate a payment — returns 202 with a paymentId and status PENDING
curl -X POST http://localhost:8081/api/v1/payments \
  -H "Content-Type: application/json" \
  -d '{"debtorIban":"TR001","creditorIban":"TR002","amount":750.00,"currency":"TRY"}'

# poll it — within a moment the listener flips it to SETTLED
curl http://localhost:8081/api/v1/payments/{paymentId}
```

## API

| Method | Path                          | Description                        |
|--------|-------------------------------|------------------------------------|
| POST   | `/api/v1/payments`            | Initiate a payment (`202 Accepted`) |
| GET    | `/api/v1/payments/{id}`       | Fetch current payment state        |

Validation failures return `400`, unknown payment ids return `404`, both as RFC 7807 `ProblemDetail` responses.

## Topics

| Topic                | Produced when          | Key         |
|----------------------|------------------------|-------------|
| `payments.initiated` | a payment is accepted  | `paymentId` |
| `payments.settled`   | a payment is settled   | `paymentId` |

Keying events by `paymentId` keeps all events for one payment on the same partition, so their ordering is preserved end to end.

## Tests

```bash
mvn test
```

`PaymentServiceTest` covers the state transitions and event publication with Mockito. `PaymentFlowIntegrationTest` spins up an embedded Kafka broker and asserts that an initiated payment is driven to `SETTLED` by the real listener — an end-to-end check of the async path without any external infrastructure.
