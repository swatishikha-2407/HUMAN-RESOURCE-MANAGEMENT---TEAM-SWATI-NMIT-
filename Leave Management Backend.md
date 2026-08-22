# Leave Management Backend

A Spring Boot REST backend for employee leave management, payroll, attendance, analytics, and reporting. The implementation uses Java 17+, Spring Boot 3, Spring Data JPA, MySQL, Flyway database migrations, BCrypt password hashing, and stateless JWT authentication.

## Functional coverage

| Area | Employee | Admin |
|---|---|---|
| Leave | Apply leave, choose Paid/Sick/Unpaid, choose dates, add remarks, view status | View all requests, approve/reject, add comment |
| Payroll | Read-only view of own salary records | View payroll, update salary structure, manage payroll |
| Attendance | View own date-range history | Add or correct employee attendance |
| Analytics | Not exposed | Employee count, present/absent totals, leave statistics, daily attendance chart data, payroll overview |

## Requirements

Install Java 17 or newer, Maven 3.9 or newer, and Docker Desktop or a local MySQL 8 database. The project has been compiled successfully with Maven and Java 21 in the development environment.

## Run locally

Start MySQL with Docker Compose:

```bash
docker compose up -d mysql
```

Then start the API:

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Flyway automatically creates the schema from `src/main/resources/db/migration/V1__initial_schema.sql`.

To use another database, set the variables in `.env.example` in your shell or deployment platform. Do not commit a real `.env` file or production JWT secret.

## Development users

The development seeder creates these accounts only when the database is empty. Change or remove `DataSeeder.java` before production deployment.

| Role | Email | Password |
|---|---|---|
| Admin | `admin@example.com` | `Admin@123` |
| Employee | `jane@example.com` | `Employee@123` |

## Authentication

Login first:

```bash
curl -X POST http://localhost:8080/api/auth/login \\
  -H 'Content-Type: application/json' \\
  -d '{"email":"admin@example.com","password":"Admin@123"}'
```

Use the returned token on protected requests:

```bash
-H "Authorization: Bearer YOUR_TOKEN"
```

## REST endpoints

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/auth/login` | Public | Authenticate and return JWT |
| POST | `/api/leaves` | Employee | Apply for Paid, Sick, or Unpaid leave |
| GET | `/api/leaves/mine` | Employee | View own requests and current statuses |
| GET | `/api/leaves` | Admin | View all leave requests |
| PATCH | `/api/leaves/{id}/review` | Admin | Approve or reject and save a comment |
| GET | `/api/payroll/mine` | Employee | Read-only own salary records |
| GET | `/api/payroll` | Admin | View all payroll records |
| PUT | `/api/payroll/employees/{employeeId}` | Admin | Create or update salary structure for a pay period |
| GET | `/api/attendance/mine` | Employee | View own attendance; optional `from` and `to` ISO dates |
| PUT | `/api/attendance/employees/{employeeId}` | Admin | Create or update daily attendance |
| GET | `/api/analytics/dashboard` | Admin | Return dashboard cards, chart series, leave statistics, and payroll overview |
| GET | `/actuator/health` | Public | Health check |

## Example payloads

Employee leave application:

```json
{
  "leaveType": "PAID",
  "startDate": "2026-09-14",
  "endDate": "2026-09-16",
  "remarks": "Family commitment"
}
```

Admin leave review:

```json
{
  "status": "APPROVED",
  "adminComment": "Approved by department manager"
}
```

Admin payroll update:

```json
{
  "payPeriod": "2026-09",
  "basicSalary": 5000.00,
  "allowances": 500.00,
  "deductions": 250.00
}
```

Admin attendance entry:

```json
{
  "attendanceDate": "2026-09-01",
  "status": "PRESENT",
  "checkIn": "09:00:00",
  "checkOut": "17:00:00",
  "notes": ""
}
```

Analytics request:

```bash
curl 'http://localhost:8080/api/analytics/dashboard?from=2026-09-01&to=2026-09-30' \\
  -H "Authorization: Bearer ADMIN_TOKEN"
```

The response includes `employeeCount`, `presentCount`, `absentCount`, `leaveByStatus`, `leaveByType`, `attendanceDaily`, and `payrollOverview`. `attendanceDaily` is suitable for a line or bar chart, while `payrollOverview` is suitable for a monthly payroll chart.

## Database tables

The migration creates `users`, `leave_requests`, `payroll_records`, and `attendance_records`. Foreign keys preserve employee and reviewer relationships. Unique constraints prevent duplicate attendance for an employee/date and duplicate payroll records for an employee/pay-period. Indexes support employee history, status dashboards, date-range attendance, and payroll-period summaries.

## Important production changes

Use a long random `JWT_SECRET`, remove development seed passwords, restrict CORS to the deployed frontend, use a non-root MySQL account, place the API behind HTTPS, and add audit logging before production use. The current implementation intentionally keeps employee payroll endpoints read-only by exposing only GET operations to the Employee role; payroll mutation is protected with `ROLE_ADMIN`.

## Build and test

```bash
mvn clean test
```

The repository is organized as a conventional Maven project. Commit the contents of this directory to GitHub, excluding `target/` and `.env`.
