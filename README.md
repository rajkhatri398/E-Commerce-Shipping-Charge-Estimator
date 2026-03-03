# Shipping Estimator - B2B E-Commerce Shipping Charge Calculator

A production-quality Spring Boot backend service that calculates shipping charges for a B2B e-commerce marketplace serving Kirana stores.

---

## 📋 Table of Contents

- [Architecture Overview](#architecture-overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Business Rules](#business-rules)
- [Design Patterns](#design-patterns)
- [How to Run](#how-to-run)
- [Testing](#testing)
- [Sample Data](#sample-data)

---

## 🏗️ Architecture Overview

The application follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────┐
│                  Controller Layer                │
│         (REST API endpoints)                     │
├─────────────────────────────────────────────────┤
│                  Service Layer                   │
│     (Business logic & orchestration)             │
├─────────────────────────────────────────────────┤
│              Strategy + Factory                  │
│  (Transport mode selection & charge calculation) │
├─────────────────────────────────────────────────┤
│                Repository Layer                  │
│          (Data access via Spring Data JPA)        │
├─────────────────────────────────────────────────┤
│                  Model Layer                     │
│           (JPA Entities & DTOs)                   │
├─────────────────────────────────────────────────┤
│                   H2 Database                    │
│           (In-memory, auto-populated)            │
└─────────────────────────────────────────────────┘
```

### Key Architectural Decisions

1. **Interface-based Services**: All services have interfaces with implementations, enabling easy testing and future extension.
2. **Global Exception Handling**: `@ControllerAdvice` provides consistent error responses across all endpoints.
3. **Caching**: Spring Cache is used to cache nearest warehouse results for faster repeated lookups.
4. **Validation**: Jakarta Bean Validation ensures request integrity at the controller level.

---

## 🛠️ Tech Stack

| Technology        | Purpose                           |
|-------------------|-----------------------------------|
| Java 17           | Programming Language              |
| Spring Boot 3.2   | Application Framework             |
| Spring Web        | REST API development              |
| Spring Data JPA   | Data persistence layer            |
| Hibernate         | ORM framework                     |
| H2 Database       | In-memory database (dev/test)     |
| Lombok            | Boilerplate code reduction        |
| Spring Cache      | Caching nearest warehouse results |
| JUnit 5           | Unit testing framework            |
| Mockito           | Mocking framework for tests       |
| Maven             | Build and dependency management   |

---

## 📂 Project Structure

```
shipping-estimator/
├── pom.xml                                     # Maven configuration
├── README.md                                   # This file
└── src/
    ├── main/
    │   ├── java/com/jumbotail/shippingestimator/
    │   │   ├── ShippingEstimatorApplication.java   # Main entry point
    │   │   ├── config/
    │   │   │   ├── CacheConfig.java                # Cache configuration
    │   │   │   └── DataLoader.java                 # Sample data loader
    │   │   ├── controller/
    │   │   │   ├── HealthController.java            # Root & health check endpoints
    │   │   │   ├── WarehouseController.java        # Warehouse API endpoints
    │   │   │   └── ShippingChargeController.java   # Shipping charge API endpoints
    │   │   ├── dto/
    │   │   │   ├── NearestWarehouseResponse.java   # Nearest warehouse response DTO
    │   │   │   ├── ShippingChargeResponse.java     # Shipping charge response DTO
    │   │   │   ├── ShippingCalculateRequest.java   # Calculate request DTO
    │   │   │   ├── ShippingCalculateResponse.java  # Calculate response DTO
    │   │   │   ├── WarehouseLocationDto.java       # Warehouse location DTO
    │   │   │   └── ErrorResponse.java              # Error response DTO
    │   │   ├── exception/
    │   │   │   ├── GlobalExceptionHandler.java     # @ControllerAdvice handler
    │   │   │   ├── ResourceNotFoundException.java  # 404 exception
    │   │   │   ├── InvalidParameterException.java  # 400 exception
    │   │   │   └── NoWarehouseAvailableException.java
    │   │   ├── factory/
    │   │   │   └── TransportModeFactory.java       # Factory for transport strategies
    │   │   ├── model/
    │   │   │   ├── Customer.java                   # Customer entity
    │   │   │   ├── Seller.java                     # Seller entity
    │   │   │   ├── Product.java                    # Product entity
    │   │   │   └── Warehouse.java                  # Warehouse entity
    │   │   ├── repository/
    │   │   │   ├── CustomerRepository.java
    │   │   │   ├── SellerRepository.java
    │   │   │   ├── ProductRepository.java
    │   │   │   └── WarehouseRepository.java
    │   │   ├── service/
    │   │   │   ├── WarehouseService.java           # Warehouse service interface
    │   │   │   ├── ShippingChargeService.java      # Shipping service interface
    │   │   │   └── impl/
    │   │   │       ├── WarehouseServiceImpl.java    # Warehouse service implementation
    │   │   │       └── ShippingChargeServiceImpl.java
    │   │   ├── strategy/
    │   │   │   ├── TransportStrategy.java          # Strategy interface
    │   │   │   ├── MiniVanStrategy.java            # 0-100 km: 3 Rs/km/kg
    │   │   │   ├── TruckStrategy.java              # 100-500 km: 2 Rs/km/kg
    │   │   │   └── AeroplaneStrategy.java          # 500+ km: 1 Rs/km/kg
    │   │   └── util/
    │   │       └── DistanceCalculator.java         # Haversine distance formula
    │   └── resources/
    │       └── application.yml                     # Application configuration
    └── test/
        └── java/com/jumbotail/shippingestimator/
            ├── ShippingEstimatorApplicationTests.java
            ├── controller/
            │   ├── WarehouseControllerTest.java
            │   └── ShippingChargeControllerTest.java
            ├── factory/
            │   └── TransportModeFactoryTest.java
            ├── service/
            │   ├── WarehouseServiceTest.java
            │   └── ShippingChargeServiceTest.java
            └── util/
                └── DistanceCalculatorTest.java
```

---

## 📡 API Documentation

### Health & Root

| Endpoint      | Method | Description                        |
|---------------|--------|------------------------------------|
| `/`           | GET    | Service info and endpoint listing  |
| `/health`     | GET    | Health check (`{"status":"UP"}`)  |

---

### 1. Get Nearest Warehouse

Find the nearest warehouse to a seller's location.

**Endpoint:** `GET /api/v1/warehouse/nearest`

| Parameter   | Type | Required | Description          |
|-------------|------|----------|----------------------|
| `sellerId`  | Long | Yes      | The seller's ID      |
| `productId` | Long | Yes      | The product's ID     |

**Sample Request:**
```
GET /api/v1/warehouse/nearest?sellerId=1&productId=1
```

**Sample Response:**
```json
{
  "warehouseId": 1,
  "warehouseLocation": {
    "lat": 12.9716,
    "long": 77.5946
  }
}
```

---

### 2. Get Shipping Charge

Calculate shipping charge from a specific warehouse to a customer.

**Endpoint:** `GET /api/v1/shipping-charge`

| Parameter       | Type   | Required | Description                      |
|-----------------|--------|----------|----------------------------------|
| `warehouseId`   | Long   | Yes      | Source warehouse ID              |
| `customerId`    | Long   | Yes      | Destination customer ID          |
| `productId`     | Long   | Yes      | Product ID (for weight info)     |
| `deliverySpeed` | String | Yes      | `standard` or `express`          |

**Sample Request:**
```
GET /api/v1/shipping-charge?warehouseId=1&customerId=2&productId=3&deliverySpeed=standard
```

**Sample Response:**
```json
{
  "shippingCharge": 150.00
}
```

---

### 3. Calculate Shipping Charge (End-to-End)

Combines nearest warehouse lookup and shipping charge calculation.

**Endpoint:** `POST /api/v1/shipping-charge/calculate`

**Request Body:**
```json
{
  "sellerId": 1,
  "customerId": 2,
  "productId": 3,
  "deliverySpeed": "express"
}
```

**Sample Response:**
```json
{
  "shippingCharge": 180.00,
  "nearestWarehouse": {
    "warehouseId": 1,
    "warehouseLocation": {
      "lat": 12.9716,
      "long": 77.5946
    }
  }
}
```

---

## 📏 Business Rules

### Transport Mode Selection (based on distance)

| Transport Mode | Distance Range | Rate             |
|----------------|---------------|------------------|
| Mini Van       | 0 – 100 km    | 3 Rs per km/kg   |
| Truck          | 100 – 500 km  | 2 Rs per km/kg   |
| Aeroplane      | 500+ km       | 1 Rs per km/kg   |

### Delivery Speed Charges

| Speed    | Formula                                                        |
|----------|---------------------------------------------------------------|
| Standard | ₹10 base charge + (rate × distance × weight)                  |
| Express  | ₹10 base charge + (₹1.2 × weight) + (rate × distance × weight)|

### Distance Calculation

Uses the **Haversine Formula** to calculate great-circle distance between two geographic coordinates:

```
a = sin²(Δlat/2) + cos(lat1) × cos(lat2) × sin²(Δlng/2)
c = 2 × atan2(√a, √(1−a))
d = R × c    (where R = 6371 km)
```

---

## 🎨 Design Patterns

### 1. Strategy Pattern

Used for transport mode-based charge calculation. Each transport mode (MiniVan, Truck, Aeroplane) implements the `TransportStrategy` interface with its own rate logic.

```
TransportStrategy (interface)
├── MiniVanStrategy    → 3 Rs/km/kg
├── TruckStrategy      → 2 Rs/km/kg
└── AeroplaneStrategy  → 1 Rs/km/kg
```

### 2. Factory Pattern

`TransportModeFactory` selects the appropriate strategy based on distance:
- 0–100 km → MiniVanStrategy
- 100–500 km → TruckStrategy
- 500+ km → AeroplaneStrategy

### 3. Builder Pattern

Used via Lombok's `@Builder` for clean object construction across entities and DTOs.

---

## 🚀 How to Run

### Prerequisites

- Java 17+
- Maven 3.8+

### Steps

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd shipping-estimator
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application:**
   - API Base URL: `http://localhost:8080/api/v1`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:shippingdb`
     - Username: `sa`
     - Password: (empty)

### Quick Test with cURL

```bash
# 1. Get nearest warehouse
curl "http://localhost:8080/api/v1/warehouse/nearest?sellerId=1&productId=1"

# 2. Get shipping charge
curl "http://localhost:8080/api/v1/shipping-charge?warehouseId=1&customerId=2&productId=3&deliverySpeed=standard"

# 3. Calculate shipping charge (end-to-end)
curl -X POST "http://localhost:8080/api/v1/shipping-charge/calculate" \
  -H "Content-Type: application/json" \
  -d '{"sellerId":1,"customerId":2,"productId":3,"deliverySpeed":"express"}'
```

---

## 🧪 Testing

### Run all tests:
```bash
mvn test
```

### Test Coverage (45 tests, all passing):

| Test Class                      | What it Tests                                    |
|---------------------------------|--------------------------------------------------|
| `DistanceCalculatorTest`        | Haversine formula accuracy, symmetry, edge cases |
| `TransportModeFactoryTest`      | Strategy selection by distance, charge calc      |
| `WarehouseServiceTest`          | Nearest warehouse logic, error handling          |
| `ShippingChargeServiceTest`     | Charge calculation, delivery speeds, validation  |
| `WarehouseControllerTest`       | API endpoints, error responses (MockMvc)         |
| `ShippingChargeControllerTest`  | API endpoints, validation, error handling        |

---

## 📊 Sample Data

The application auto-loads sample data on startup via `DataLoader`:

### Warehouses (5)
| ID | Name           | City       |
|----|----------------|------------|
| 1  | BLR_Warehouse  | Bangalore  |
| 2  | MUMB_Warehouse | Mumbai     |
| 3  | DEL_Warehouse  | Delhi      |
| 4  | HYD_Warehouse  | Hyderabad  |
| 5  | CHN_Warehouse  | Chennai    |

### Sellers (4)
| ID | Name                   | City       |
|----|------------------------|------------|
| 1  | Nestle India Seller    | Bangalore  |
| 2  | Rice Wholesale Seller  | Mumbai     |
| 3  | Sugar Trading Seller   | Delhi      |
| 4  | Spice World Seller     | Hyderabad  |

### Products (7)
| ID | Name                  | Seller | Weight |
|----|-----------------------|--------|--------|
| 1  | Maggie 500g Packet    | 1      | 0.5 kg |
| 2  | Nescafe Coffee 200g   | 1      | 0.25 kg|
| 3  | Rice Bag 10Kg         | 2      | 10 kg  |
| 4  | Rice Bag 25Kg         | 2      | 25 kg  |
| 5  | Sugar Bag 25Kg        | 3      | 25 kg  |
| 6  | Turmeric Powder 1Kg   | 4      | 1 kg   |
| 7  | Red Chilli Powder 5Kg | 4      | 5 kg   |

### Customers (6)
| ID | Name                  | City       |
|----|-----------------------|------------|
| 1  | Shree Kirana Store    | Belgaum    |
| 2  | Andheri Mini Mart     | Mumbai     |
| 3  | Rajesh General Store  | Delhi      |
| 4  | Lakshmi Provision     | Chennai    |
| 5  | Ganesh Traders        | Hyderabad  |
| 6  | Punjab Kirana Corner  | Chandigarh |

---

## ⚠️ Error Handling

All errors return a consistent JSON structure:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Seller not found with id: 999",
  "path": "/api/v1/warehouse/nearest",
  "timestamp": "2026-03-03 10:30:00"
}
```

| Scenario                    | HTTP Status |
|-----------------------------|-------------|
| Resource not found          | 404         |
| Invalid delivery speed      | 400         |
| Missing required parameter  | 400         |
| Parameter type mismatch     | 400         |
| Validation failure          | 400         |
| No warehouses available     | 404         |
| Unknown path / endpoint     | 404         |
| Unexpected server error     | 500         |

---

## 📜 License

This project is built for the Jumbotail Backend Engineering assignment.
