# 📦 PCMS Demo (Product Content Management System)

A professional Product Content Management System built with **Spring Boot** and **PostgreSQL**. This project demonstrates a highly flexible approach to managing product attributes using **JSONB** and a dynamic filtering engine powered by the **Strategy Pattern**.

---

## 🚀 Key Features

* **Dynamic Attribute Management**: Store and update complex product data using PostgreSQL **JSONB** without schema changes.
* **Smart Filtering Engine**: Search products by name or any JSONB attribute using dedicated strategies:
    * `_min` / `_max`: Numerical range filtering.
    * `has_attr`: Key existence validation.
    * `comma-separated`: Multi-value (OR logic / IN clause) matching.
* **Reserved Keywords Protection**: Automatic validation preventing users from creating attributes that conflict with filtering logic (e.g., suffixes like `_min`).
* **Performance Optimized**: 
    * JPQL Constructor Expressions for efficient DTO projection.
    * Batch processing (`batch_size: 25`) for bulk inserts.
    * Read-only transactions for search operations.

---

## 🛠 Tech Stack

* **Java 21**
* **Spring Boot 3.5.13**
* **PostgreSQL 18** (with JSONB support)
* **Liquibase** (Database migration)
* **MapStruct** (Entity-DTO mapping)
* **Lombok**
* **SpringDoc / Swagger UI**

---

## ⚙️ Prerequisites

* **JDK 21** or higher.
* **Maven** (or use the provided `./mvnw` wrapper).
* **PostgreSQL** (Local or Docker).

---

## 🗄 Database & Setup

### 1. Database Creation
Create a database named `pcms_demo` in your PostgreSQL instance.

### 2. Configuration
Update the credentials in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5434/pcms_demo #change port for your database if needed
    username: admin_123
    password: 456_admin
```
### 3. Data Initialization
A sample script is available at `src/main/resources/templates/init_db.sql`. 
It populates the database with:
* **100 Producers**
* **1,000 Products** with randomized, realistic JSONB attributes (color, weight, material, voltage, etc.).

---

## 🏃 Running the Project

1. **Build and Run:**
   Linux / macOS / Git Bash:
   ```bash
   ./mvnw spring-boot:run
   ```
   Windows (Command Prompt):
   ```bash
   mvnw spring-boot:run
   ```
   Windows (PowerShell):
   ```bash
   .\mvnw spring-boot:run
   ```
2. **Access Swagger UI:**
   Navigate to
   ```bash
   http://localhost:8080/swagger-ui.html
   ```
   to explore and test the API endpoints.
3. **Exit application:**
   to exit press `Ctrl` + `C`
   
   ## 🔍 API Filtering Examples

The filtering engine is case-insensitive and supports complex combinations:

* **Name & Color:** `?name=Pro&color=blue`
* **Weight Range:** `?weight_min=1.5&weight_max=10.0`
* **Multi-origin:** `?made_in=Poland,Germany,Japan`
* **Check Attribute Existence:** `?has_attr=is_waterproof,wifi_support`

---

## 📂 Architecture Note

The project follows **Clean Architecture** and **SOLID** principles:

* **Strategy Pattern**: Used for filter resolution (`ProductFilterStrategy`). Each strategy is responsible for its own validation and SQL specification.
* **Specification API**: Used for dynamic SQL generation from JSONB fields.
* **DTO Pattern**: Clear separation between database models and API responses to avoid over-posting and data leakage.
