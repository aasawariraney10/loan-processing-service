# Loan Evaluation Service

> A production-ready Spring Boot REST service that evaluates loan applications and
determines whether a single loan offer (based on requested tenure) can be
approved.

---

## 🚀 Features

- Loan application processing
- Input validation using annotations
- EMI calculation using financial formula
- Risk band classification
- Interest rate calculation (multi-factor)
- Eligibility checks
- PostgreSQL integration
- Flyway versioned database migrations
- Global exception handling
- Clean layered architecture

---

## 🛠️ Tech Stack

- Java 17
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Flyway
- Lombok

---

## 📁 Project Structure

```
com.loan.evaluation
├── controller
├── service
│   └── impl
├── repository
├── entity
├── dto
├── enums
├── util
├── constants
└── exception
```

---

## ⚙️ Setup Instructions

### 1. Clone Repository

```
git clone https://github.com/<your-username>/loan-processing-service.git
cd loan-processing-service
```

---

### 2. Configure Environment Variables

Set the following variables:
```
DB_HOST=localhost
DB_PORT=5433
DB_NAME=loan_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

---

### 3. Run Application

```
mvn spring-boot:run
```

Application will start at:
```
http://localhost:8080
```
---

## 📡 API Endpoint

### Create Loan Application

```http
POST /applications
```


## 📥 Request Body

```json
{
  "applicant": {
    "name": "John Doe",
    "age": 30,
    "monthlyIncome": 75000,
    "employmentType": "SALARIED",
    "creditScore": 720
  },
  "loan": {
    "amount": 500000,
    "tenureMonths": 36,
    "purpose": "PERSONAL"
  }
}
```


## 📤 Response

### ✅ Approved
```json
{
  "applicationId": "uuid",
  "status": "APPROVED",
  "riskBand": "MEDIUM",
  "offer": {
    "interestRate": 13.5,
    "tenureMonths": 36,
    "emi": 16234.23,
    "totalPayable": 584432.23
  }
}
```

### ❌ Rejected
```json
{
  "applicationId": "uuid",
  "status": "REJECTED",
  "rejectionReasons": [
    "LOW_CREDIT_SCORE",
    "EMI_EXCEEDS_60_PERCENT"
  ]
}
```

---

## 🗄️ Database 

- PostgreSQL used for persistence  
- Flyway used for version-controlled schema migration  
- Migration scripts located in:  

```
src/main/resources/db/migration
```

---

## 🧠 Design Decisions

- Layered architecture (Controller → Service → Repository)  
- Constructor-based dependency injection  
- BigDecimal used for financial precision  
- Enum usage for type safety (Status, RiskBand, EmploymentType)  
- Utility class for calculation logic  
- Constants used for rejection reasons  
- Centralized exception handling using @RestControllerAdvice  

---

## 🧪 Testing

- Unit tests implemented using JUnit and Mockito
- Service layer tested for eligibility and business rules
- Controller layer tested using MockMvc