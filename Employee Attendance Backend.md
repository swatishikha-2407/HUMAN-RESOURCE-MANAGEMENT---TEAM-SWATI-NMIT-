# Employee Attendance Backend

This repository contains a Spring Boot REST backend for employee management and attendance tracking. It provides MySQL persistence, Flyway migrations, BCrypt password hashing, JWT authentication, role-based authorization, employee profile CRUD, employee check-in/check-out, attendance history, daily/weekly views, and administrative attendance updates.

## Technology stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3 |
| Persistence | Spring Data JPA / Hibernate |
| Database | MySQL 8 |
| Migrations | Flyway |
| Authentication | Spring Security + JWT |
| API documentation | Springdoc OpenAPI / Swagger UI |
| Build | Maven |

## Database design

The migration creates three related tables:

| Table | Relationship | Purpose |
|---|---|---|
| `users` | One user has one profile and many attendance rows | Login identity, employee ID, email, password hash, and role |
| `employee_profiles` | One-to-one with `users` | Personal and job details |
| `attendance` | Many-to-one with `users` | One daily attendance record per employee |

The database has a unique constraint on `(employee_id, attendance_date)`, which prevents duplicate attendance records for the same employee on the same day. Passwords are never stored as plain text.

## Run locally

First, copy `.env.example` to `.env` and replace the database password and JWT secret. The application reads the environment variables listed in that file.

Start MySQL with Docker Compose:

```bash
docker compose up -d mysql
```

Then start the API:

```bash
./mvnw spring-boot:run
# or, if Maven is installed globally:
mvn spring-boot:run
```

Flyway automatically creates the tables on the first startup. The API runs at `http://localhost:8080`. Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

### Optional first administrator

Public registration always creates an `EMPLOYEE` account. To create the first administrator, set these variables before starting the application:

```bash
APP_ADMIN_EMPLOYEE_ID=ADM001
APP_ADMIN_EMAIL=admin@example.com
APP_ADMIN_PASSWORD=change-this-password
```

The seeder runs only when the configured email does not already exist. Do not commit real credentials to GitHub.

## Authentication

Login at `POST /api/auth/login` and send the returned token in every protected request:

```http
Authorization: Bearer <token>
```

### Public endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Register an employee account |
| `POST` | `/api/auth/login` | Authenticate and receive a JWT |

Registration example:

```json
{
  "employeeId": "EMP101",
  "email": "ananya@example.com",
  "password": "strong-password",
  "name": "Ananya",
  "phone": "+91-9000000000",
  "address": "Bangalore",
  "department": "Engineering",
  "designation": "Software Engineer",
  "joiningDate": "2026-08-22",
  "profilePicture": "https://example.com/profile.jpg"
}
```

## Employee endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/employees/me` | Authenticated | Current user's profile |
| `POST` | `/api/employees` | ADMIN, HR | Create an employee |
| `GET` | `/api/employees` | ADMIN, HR | List all employees |
| `GET` | `/api/employees/{employeeId}` | ADMIN, HR | Get one employee |
| `PUT` | `/api/employees/{employeeId}` | ADMIN, HR | Update profile fields |
| `DELETE` | `/api/employees/{employeeId}` | ADMIN | Delete an employee |

`joiningDate` must use the ISO format `yyyy-MM-dd`.

## Attendance endpoints

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/attendance/check-in` | Authenticated | Creates today's `PRESENT` record |
| `POST` | `/api/attendance/check-out` | Authenticated | Saves today's checkout time |
| `GET` | `/api/attendance/me/today` | Authenticated | Returns today's record or `null` |
| `GET` | `/api/attendance/me/history` | Authenticated | Own history; optional `from` and `to` |
| `GET` | `/api/attendance/me/weekly` | Authenticated | Own Monday-Sunday week; optional `weekStart` |
| `GET` | `/api/attendance/all` | ADMIN, HR | All employees in a date range |
| `PATCH` | `/api/attendance/{attendanceId}/status` | ADMIN, HR | Set `PRESENT`, `ABSENT`, `HALF_DAY`, or `LEAVE` |

Examples:

```bash
curl -X POST http://localhost:8080/api/attendance/check-in \
  -H "Authorization: Bearer <token>"

curl "http://localhost:8080/api/attendance/me/history?from=2026-08-01&to=2026-08-22" \
  -H "Authorization: Bearer <token>"

curl "http://localhost:8080/api/attendance/me/weekly?weekStart=2026-08-17" \
  -H "Authorization: Bearer <token>"
```

The service rejects duplicate check-ins, duplicate check-outs, checkout without check-in, invalid date ranges, and attendance ranges longer than one year. The database also enforces one attendance row per employee per date.

## GitHub workflow

Do not commit `.env`, real passwords, or production JWT secrets. Commit the source code, `pom.xml`, migration files, `.env.example`, `docker-compose.yml`, and this README. A frontend team can call the endpoints above and store the JWT in its chosen secure client-side strategy.
