# Dayflow Frontend

Dayflow is a standalone JavaFX frontend demo for a workforce management system. It includes the employee experience, the Admin/HR experience, shared navigation, and reusable UI helpers in a single Java application.

> This repository contains frontend code and mock in-memory data. It does not include a backend, database, real authentication, payroll processing, or API integration. Replace the mock actions with HTTP requests when the backend is available.

## Included screens

### Authentication

The authentication flow includes Login, Sign Up, email-verification states, validation messages, loading feedback during sign-in, and demo account shortcuts.

### Employee workspace

The employee workspace includes the Dashboard, Profile, Attendance, Leave Requests, Salary, Notifications, and Logout screens. Employees can edit contact details, select a profile picture, check in and check out, submit leave requests, and view a read-only salary summary.

### Admin/HR workspace

The Admin/HR workspace includes the Admin Dashboard, Employee Management, Admin Attendance, Leave Approval, Payroll, Analytics/Reports, Notifications, and Logout screens. The demo includes search and filtering, leave approval and rejection, salary editing, report actions, status badges, and summary cards.

## Demo accounts

| Role | Email | Password |
|---|---|---|
| Admin / HR | `admin@dayflow.com` | `Admin123` |
| Employee | `employee@dayflow.com` | `Employee123` |

## Requirements

Java 21 and JavaFX 21 are recommended. Maven downloads the JavaFX dependencies automatically. If you use manual compilation, install a JavaFX SDK and point `--module-path` to its `lib` directory.

## Run with Maven

```bash
mvn clean javafx:run
```

## Compile and run manually

Linux/macOS:

```bash
mkdir -p build/classes
javac -d build/classes \
  --module-path /path/to/javafx-sdk/lib \
  --add-modules javafx.controls \
  src/main/java/com/dayflow/DayflowApp.java

java -cp build/classes \
  --module-path /path/to/javafx-sdk/lib \
  --add-modules javafx.controls \
  com.dayflow.DayflowApp
```

Windows PowerShell uses the same Java commands, but replace the path with the location of your JavaFX SDK `lib` folder.

## Project structure

```text
 dayflow-frontend/
 ├── pom.xml
 ├── README.md
 ├── .gitignore
 └── src/
     └── main/
         └── java/
             └── com/
                 └── dayflow/
                     └── DayflowApp.java
```

## Connecting a backend later

The UI actions currently update local demo objects or show a success message. When your backend team provides APIs, replace those handlers with service calls for login, signup, verification, employee records, attendance, leave requests, salary data, notifications, and reports. Keep password hashing, authorization, payroll calculations, and persistence on the backend rather than in this frontend application.
