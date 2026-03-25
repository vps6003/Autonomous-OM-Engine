---

## 🧩 Core Features

### 📦 Product Module
- Create product
- Get product by ID
- Paginated product listing
- Stock management

### 🧾 Order Module
- Create order with multiple items
- Real-time stock validation
- Order state machine
- Transactional consistency

### 🤖 Agentic AI Capabilities
- Natural language → structured actions
- Tool-based execution (no hallucinated actions)
- Handles success/failure flows
- Structured outputs: `ORDER_SUCCESS`, `ORDER_FAILED`

---

## ⚙️ Tech Stack

- Backend: Java, Spring Boot  
- Architecture: Hexagonal + DDD  
- Database: PostgreSQL  
- ORM: Spring Data JPA  
- Migration: Flyway  
- AI Layer: LLM + custom orchestrator loop  
- API: RESTful services  

---

## 📡 APIs

https://autonomous-om-engine.onrender.com/swagger-ui/index.html

### Product APIs
```http
POST https://autonomous-om-engine.onrender.com/products
GET https://autonomous-om-engine.onrender.com/products/{id}    ( id is to be specific for product)
GET https://autonomous-om-engine.onrender.com/products?page=0&size=10
