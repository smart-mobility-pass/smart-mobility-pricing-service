# 🧮 Smart Mobility Pricing Service

The **Pricing Service** is the central calculation engine of the Smart Mobility platform. Its sole responsibility is to accurately compute the price of a trip based on predefined business rules, transport types (BUS, TER, BRT), and active user subscriptions.

It acts strictly as an mathematical engine: **it does not execute payments, modify balances, or send user notifications**.

---

## 🏗️ Architecture & Responsibilities

1. **Listen to Events**: Consumes `TripCompletedEvent` from the `trip.exchange` (RabbitMQ).
2. **Fetch Dependencies**: Uses OpenFeign to contact the **User Service** and retrieve the user's active subscription status.
3. **Calculate Base Price**: Computes the initial cost based on rules configured per transport type (BUS scaling by section, TER scaling by section, BRT fixed or multi-zone).
4. **Apply Discounts**: Uses a priority-based discount engine (e.g., Subscriptions process before Off-peak discounts).
5. **Persist Results**: Saves the detailed calculation breakdown to its own database (`pricing_results`).
6. **Publish Pricing**: Emits a `TripPricedEvent` to the RabbitMQ exchange for downstream services (like Billing) to process.

---

## 🛠️ Technology Stack
- **Java 17**
- **Spring Boot 3.x**
- **Spring Cloud OpenFeign** (Inter-service communication)
- **Spring Data JPA & Hibernate**
- **MySQL** (Pricing Rules Database)
- **RabbitMQ** (Event-driven AMQP messaging)
- **Spring Security OAuth2 Resource Server** (Keycloak JWT Validation)
- **Lombok & MapStruct/Jackson**

---

## ⚙️ Configuration & Execution

### 1. Prerequisites
- RabbitMQ must be running on `localhost:5672`.
- Eureka Naming Server must be running on `localhost:8761`.
- Keycloak must be running on `localhost:8080/realms/gark-realm`.
- MySQL Server must be available on `localhost:3306`.

### 2. Application Properties
The service relies on `src/main/resources/application.properties`. It will auto-generate the database `smart_mobility_pricing` on startup.

### 3. Run the Service
You can run the service locally using Maven:
```bash
./mvnw clean spring-boot:run
```
*(The service runs by default on port **8083**)*

---

## 🗄️ Database Entities (Domain Models)

The Pricing Service manages its own pricing rules internally to allow dynamic updates without redeployment.

- **`TransportLine`**: Represents a transport line (e.g., "Ligne 1") and its type (BUS, TER, BRT).
- **`FareSection`**: Defines incremental costs per section for BUS and TER.
- **`Zone`**: Defines BRT zones.
- **`DiscountRule`**: Defines dynamic discounts (`rule_type`, `percentage`, `priority`, `condition`).
  - *Example*: A `MONTHLY` subscription grants a 30% discount at Priority 1.
- **`PricingResult`**: The final receipt/log of a calculated trip.

---

## 🔐 Internal API Endpoints

The service exposes administrative endpoints protected by Spring Security (requires a JWT with the `ROLE_ADMIN` role). These endpoints allow you to configure pricing rules on the fly.

### 🚌 Transport Lines Config
- `POST /admin/transport-lines`: Create a line.
- `GET /admin/transport-lines`: List lines.

### 🎟️ Fare Sections Config (BUS/TER)
- `POST /admin/fare-sections`: Define a section cost.
- `GET /admin/fare-sections`: List defined sections.

### 📍 BRT Zones Config
- `POST /admin/zones`: Define a zone.
- `GET /admin/zones`: List zones.

### 💸 Discount Rules Config
- `POST /admin/discount-rules`: Add a new mathematical rule.
  - Body example:
    ```json
    {
      "ruleType": "SUBSCRIPTION",
      "percentage": 30.00,
      "priority": 1,
      "condition": "MONTHLY",
      "active": true
    }
    ```
- `GET /admin/discount-rules`: View active rules.
- `DELETE /admin/discount-rules/{id}`: Remove a rule.

---

## 📡 Message Broker (RabbitMQ)

### Consumes
**Queue:** `pricing.trip.completed.queue`
**Routing Key:** `trip.completed`
```json
// TripCompletedEvent
{
  "tripId": 123,
  "userId": 456,
  "transportType": "BRT",
  "startZone": 1,
  "endZone": 2,
  "startTime": "2026-02-25T10:00:00",
  "endTime": "2026-02-25T10:45:00"
}
```

### Publishes
**Exchange:** `trip.exchange`
**Routing Key:** `trip.priced`
```json
// TripPricedEvent
{
  "tripId": 123,
  "userId": 456,
  "basePrice": 500.00,
  "appliedDiscounts": [
    {
      "ruleType": "SUBSCRIPTION",
      "percentage": 30.00,
      "amountDeducted": 150.00
    }
  ],
  "finalAmount": 350.00
}
```

---

## 🧪 Testing
Run the comprehensive test suite validating the logic engine, priority reduction application, and event mappings:
```bash
./mvnw clean test
```
