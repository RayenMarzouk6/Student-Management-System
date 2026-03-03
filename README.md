# Student Management System

This project is a Java-based Student Management System developed using JDBC and MySQL.  
<img width="1536" height="1024" alt="ChatGPT Image Mar 3, 2026, 09_26_59 PM" src="https://github.com/user-attachments/assets/346fc233-47db-4f4a-a808-1fda6047c7d3" />
It demonstrates the implementation of Object-Relational Mapping (ORM) concepts and core design patterns including:

- DAO (Data Access Object)
- Factory Pattern
- Singleton Pattern
- Bidirectional Association between entities

## Features

- Create, update, delete, and find Students
- Manage Departments
- Maintain bidirectional relationship between Student and Department
- Centralized DAO instantiation using Factory
- Single database connection using Singleton

## Technologies Used

- Java
- JDBC
- MySQL

## Architecture

- `entity` package → Entity classes (Student, Department)
- `dao` package → DAO classes + DaoFactory
- `utils` package → Singleton MySqlConnection
- Main class → Business logic testing

This project is designed as an academic exercise to understand persistence layer architecture and clean separation between business logic and data access layer.
