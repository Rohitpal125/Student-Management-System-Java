# Student Management System

## Description

Student Management System is a Java-based application that manages student records using JDBC and MySQL.

The application supports student CRUD operations, searching, sorting, filtering, statistics, validation, and exception handling.

## Features

- Add new student
- Display all students
- Search student by ID
- Search students by name
- Search students by course
- Update student details
- Delete student
- Calculate total students
- Calculate average CGPA
- Find highest and lowest CGPA
- Display students per course
- Display students per semester
- Sort students by name, CGPA, age, and semester
- Filter students by CGPA, age, semester, and course
- Student input validation
- Custom exception handling
- MySQL database integration using JDBC

## Tech Stack

- **Java** — Core application development
- **JDBC** — Java Database Connectivity
- **MySQL** — Database management
- **Maven** — Project and dependency management
- **Git** — Version control
- **GitHub** — Source code hosting
- **IntelliJ IDEA** — Development environment


## Project Architecture

The project follows a layered architecture:

```text
Main
  ↓
StudentService
  ↓
StudentDAO
  ↓
DBConnection
  ↓
MySQL Database

```

## Database

The project uses MySQL as the database.

### Tables

#### `students`

Stores student information such as:

- Student ID
- Name
- Age
- Email
- Course ID
- Semester
- Phone
- CGPA

#### `courses`

Stores course information:

- Course ID
- Course Name

The `students.course_id` column is used to connect students with their respective courses.

## How to Run
### 1. Clone the Repository

```bash
git clone https://github.com/Rohitpal125/Student-Management-System-Java.git
```
### 2. Open the Project

Open the project in IntelliJ IDEA.

### 3. Configure MySQL

Make sure MySQL is installed and running on your system.

Create the database:

```sql
CREATE DATABASE student_management_system;
```

### 4. Configure Database Password

The database password is not stored directly in the source code.

Set an environment variable named:

```text
DB_PASSWORD
```
Set its value to your local MySQL password.

### 5. Build the Project

Use Maven to build the project:

```bash
mvn clean package
```

### 6. Run the Application

Run the `Main` class from IntelliJ IDEA.