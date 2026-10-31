# Student Management System

A full-stack student record manager built with **Java, Spring Boot and MySQL**. Anyone can view students. Only a logged-in **admin** can add, edit or delete them, and ask an **AI assistant** (Google Gemini) for a short report.

## Live Demo

- **Frontend:** https://rohitpal125.github.io/Student-Management-System-Java/
- **Backend API:** https://student-management-api-e13m.onrender.com/api/students

The backend runs on free hosting, so the first load after a break can take up to a minute.

## Screenshots

![Home](screenshots/01-home.png)
![Filters](screenshots/02-filter.png)
![Admin panel](screenshots/03-admin.png)

## Features

### Web app
- View all students in a table (responsive, works on mobile)
- Search by name, email, phone or ID
- Filter by course and semester
- Sort by ID, name, CGPA, age or semester
- Summary numbers (total, average, highest and lowest CGPA) that follow the active filters
- Admin login (visitors can only view)
- Admin can add, edit and delete students
- Admin can change their own password
- **AI insights (admin only):** a short report about the students, written by Google Gemini from counts and averages
- Input validation on the page and on the server

### Console app (original version)
- Full CRUD from the terminal
- Search by ID, name or course
- Students per course and per semester
- Sorting and filtering
- Statistics (total, average, highest and lowest CGPA)

## Tech Stack

- **Java 21** and **Spring Boot 4** (REST API)
- **JDBC** with **MySQL**
- **Google Gemini API** (AI insights)
- **HTML, CSS and JavaScript** (no framework) for the frontend
- **Maven** for build and dependencies
- **Docker** for containerizing the backend
- **Git and GitHub**, **IntelliJ IDEA**

## Architecture

```text
Browser (HTML, CSS, JavaScript)
            |  fetch() with JSON
            v
REST API  (StudentController, AuthController, AiController)
            |  AuthInterceptor checks the admin token
            v
Services  (StudentService, AuthService, AiService)
            |                          |
            v                          v  HTTPS + API key
DAOs      (StudentDAO, AdminDAO)     Google Gemini API
            v
DBConnection  ->  MySQL
```

## Project Structure

```text
StudentManagementSystem/
├── pom.xml
├── Dockerfile
├── docs/                  frontend (served by GitHub Pages)
│   ├── index.html
│   ├── style.css
│   └── script.js
└── src/
    ├── database/      DBConnection, StudentDAO, AdminDAO
    ├── exception/     StudentNotFoundException, AiException
    ├── model/         Student
    ├── service/       StudentService, AuthService, AiService
    ├── util/          StudentValidator, PasswordUtil, TokenUtil, AiHelper
    └── main/
        ├── StudentManagementApplication.java   (starts the web app)
        ├── Main.java                           (console version)
        ├── controller/   StudentController, AuthController, AiController
        └── config/       WebConfig, AuthInterceptor, AdminBootstrap
```

## REST API

| Method | Endpoint | Who can use it |
|---|---|---|
| GET | `/api/students` | Everyone |
| POST | `/api/students` | Admin only |
| PUT | `/api/students/{id}` | Admin only |
| DELETE | `/api/students/{id}` | Admin only |
| POST | `/api/login` | Everyone (returns a token) |
| POST | `/api/change-password` | Admin only |
| POST | `/api/ai/insights` | Admin only |

Admin requests send the token in the header: `Authorization: Bearer <token>`.

## AI Insights

The admin presses **AI insights** and gets a short report, for example about which course has the lowest average CGPA.

How it works:
1. The backend reads the students and builds plain numbers: total, average CGPA, and per course the count, average, lowest, highest and how many are below 6.0.
2. It sends those numbers, with a short instruction, to the Google Gemini API.
3. The reply is shown in a window on the page.

Design choices:
- **Privacy:** only aggregated numbers are sent. Names, emails and phone numbers never leave the server.
- **Admin only:** free API limits are small, so only admins can trigger it.
- **No hidden data:** the prompt tells the model to use only the numbers given. The report is a helper, not a source of truth, so the admin should check important numbers.
- **Failures are handled:** timeouts, rate limits (429) and a missing or wrong key return a clear message instead of crashing the app.
- **API key** is read from an environment variable and is never in the code or the frontend.

## Security

- Passwords are never stored in plain text. They are hashed with **PBKDF2 (HMAC-SHA256)** and a random salt.
- Login returns a signed token (**HMAC-SHA256**) that expires after 8 hours.
- Write endpoints return `401 Unauthorized` without a valid token. The server enforces this, not just the frontend.
- There is **no public sign-up**. The admin account is created from environment variables.
- No secrets are stored in the source code. Database details, passwords and the AI key come from environment variables.
- CORS only allows the listed frontend origins.

## Database

MySQL with two main tables. The `admins` table is created automatically when the app starts.

```sql
CREATE TABLE courses (
  course_id INT AUTO_INCREMENT PRIMARY KEY,
  course_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE students (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  age INT NOT NULL,
  email VARCHAR(150) NOT NULL,
  course_id INT NOT NULL,
  semester INT NOT NULL,
  phone VARCHAR(15) NOT NULL,
  cgpa DOUBLE NOT NULL,
  FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

INSERT INTO courses (course_name)
VALUES ('Btech'), ('Btech AIML'), ('Nursing'), ('GNM'), ('BTC');
```

A student's course must already exist in the `courses` table.

## Environment Variables

| Variable | Purpose | Default |
|---|---|---|
| `DB_URL` | JDBC URL of the database | `jdbc:mysql://localhost:3306/student_management_system` |
| `DB_USER` | Database user | `root` |
| `DB_PASSWORD` | Database password | none |
| `ADMIN_USERNAME` | Admin username (created on first start) | none |
| `ADMIN_PASSWORD` | Admin password, at least 8 characters | none |
| `TOKEN_SECRET` | Secret used to sign login tokens, at least 32 characters | random on every start |
| `GEMINI_API_KEY` | Google Gemini API key (needed for AI insights) | none |
| `GEMINI_MODEL` | Gemini model name | `gemini-3.5-flash` |
| `RESET_ADMIN_PASSWORD` | Set to `true` for one start to reset a forgotten admin password | off |
| `ALLOWED_ORIGINS` | Comma-separated list of allowed frontend URLs | localhost and the GitHub Pages site |
| `PORT` | Port the server listens on | `10000` in Docker |

Never commit real values for these variables.

## How to Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/Rohitpal125/Student-Management-System-Java.git
```

### 2. Set up MySQL

Create the database and run the SQL from the Database section above.

```sql
CREATE DATABASE student_management_system;
USE student_management_system;
```

### 3. Set environment variables

In IntelliJ IDEA: **Run > Edit Configurations > Environment variables**.

```text
DB_PASSWORD=your_mysql_password;ADMIN_USERNAME=admin;ADMIN_PASSWORD=choose_a_strong_password;TOKEN_SECRET=at_least_32_random_characters_here
```

To try AI insights, add `GEMINI_API_KEY` (a free key from Google AI Studio).

### 4. Run the backend

Run `main.StudentManagementApplication` from IntelliJ. The API starts on `http://localhost:8080`.

Or build and run the jar:

```bash
mvn clean package
java -jar target/StudentManagementSystem-1.0-SNAPSHOT.jar
```

### 5. Open the frontend

Open `docs/index.html` from IntelliJ (right-click > Open in > Browser). Opening it from IntelliJ matters because the backend allows that origin by default.

### 6. Console version (optional)

Run the `Main` class inside the `main` package.

## Deployment

The project runs on free tiers:

- **Frontend:** GitHub Pages (the `docs` folder)
- **Backend:** Render (Docker, using the `Dockerfile` in this repo)
- **Database:** Aiven for MySQL
- **AI:** Google Gemini API (free tier)

Free services sleep when idle, and the free AI tier has small limits, so the first request after a break can be slow.

## Author

Rohit Pal