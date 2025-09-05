# 🌐 Gateway Service (Spring Cloud Gateway)

The **Gateway Service** is a **Spring Cloud Gateway** application that serves as the single entry point for all client requests.  
It routes incoming API calls to the appropriate **Student, Course, Enrollment, or Attendance** microservice.  

---

## 🚀 Features
- Centralized API gateway for all services.  
- **Routing** and **load balancing**.  
- Unified API layer for frontend clients.  
- Ready for adding **authentication, logging, and monitoring**.  

---

## 📂 Project Structure

```
/gateway-service/
│── src/
│── pom.xml
```

---

## ⚙️ Setup & Run  

### 1. Navigate to Gateway Service
```bash
cd gateway-service
```

### 2. Run Gateway
```bash
./mvnw spring-boot:run
```

### 3. Gateway Default Port
By default, the Gateway runs on **`http://localhost:8080`**  

---

## 🔗 API Routing (via Gateway)

- `http://localhost:8080/api/students` → Student Service (port 8082)  
- `http://localhost:8080/api/courses` → Course Service (port 8083)  
- `http://localhost:8080/api/enrollments` → Enrollment Service (port 8084)  
- `http://localhost:8080/api/attendances` → Attendance Service (port 8085)  

*(Configured in `application.properties`)*  

---

## 🛠 Tech Stack
- **Framework**: Spring Boot + Spring Cloud Gateway  
- **Build Tool**: Maven  
- **Communication**: REST APIs  



