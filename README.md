# ✅ TodoApp — Backend

A RESTful backend for a mobile task management application, built with **Java 21** and **Spring Boot 3.4**. Features JWT authentication, email notifications with HTML templates, and full Docker support.

> 📱 Mobile frontend: [TodoApp (Android)](https://github.com/Ginseku/TodoApp)

---

## 🚀 Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.5 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | PostgreSQL |
| ORM | Spring Data JPA |
| Email | Spring Mail + Thymeleaf templates |
| Mapping | MapStruct 1.5.5 |
| Build Tool | Gradle |
| Containerization | Docker + Docker Compose |
| Testing | JUnit 5 + Spring Security Test |
| Utilities | Lombok, Bean Validation |

---

## ✨ Features

- 🔐 **JWT Authentication** — secure registration and login
- 📧 **Email Notifications** — HTML email templates via Thymeleaf
- 📝 **Notes & Categories** — full CRUD for tasks and categories
- 👤 **User Management** — per-user data isolation
- ✅ **Input Validation** — request validation on all endpoints
- 🗺️ **DTO Mapping** — clean layer separation with MapStruct
- 🐳 **Docker Support** — containerized for easy deployment

---

## 📁 Project Structure

```
TodoAppBack/
├── src/
│   └── main/
│       ├── java/com/TodoBackend/
│       │   ├── controller/      # REST API controllers
│       │   ├── service/         # Business logic
│       │   ├── repository/      # Data access layer
│       │   ├── model/           # JPA entities
│       │   ├── dto/             # Data transfer objects
│       │   ├── security/        # JWT & Spring Security config
│       │   └── mapper/          # MapStruct mappers
│       └── resources/
│           └── templates/       # Thymeleaf email templates
├── Dockerfile
├── docker-compose.yml
└── build.gradle
```

---

## ⚙️ Getting Started

### Prerequisites

- Java 21+
- Docker & Docker Compose
- PostgreSQL database
- SMTP server (for email features)

### 1. Clone the repository

```bash
git clone https://github.com/Ginseku/TodoAppBack.git
cd TodoAppBack
```

### 2. Configure environment variables

Create a `.env` file in the project root:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tododb
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password

SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password

JWT_SECRET=your_jwt_secret_key
```

### 3. Run with Docker Compose

```bash
docker-compose up --build
```

### 4. Run locally

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`

---

## 📬 API Endpoints

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Register a new user | ❌ |
| POST | `/api/auth/login` | Login and get JWT token | ❌ |
| GET | `/api/notes` | Get all user notes | ✅ |
| POST | `/api/notes` | Create a new note | ✅ |
| PUT | `/api/notes/{id}` | Update a note | ✅ |
| DELETE | `/api/notes/{id}` | Delete a note | ✅ |
| GET | `/api/categories` | Get all categories | ✅ |
| POST | `/api/categories` | Create a category | ✅ |

> All protected endpoints require `Authorization: Bearer <token>` header

---

## 🔗 Related

- 📱 [TodoApp — Android Client](https://github.com/Ginseku/TodoApp)

---

## 👤 Author

**Mykyta Bondarchuk**
- GitHub: [@Ginseku](https://github.com/Ginseku)
- LinkedIn: [mykyta-bondarchuk](https://www.linkedin.com/in/mykyta-bondarchuk-a61150268/)
