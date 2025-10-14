# Meal Craft 🥗⛏️

<p align="center">
  <img src="https://github.com/user-attachments/assets/142b60b0-bedd-43a1-b250-7ba1f38e645e" 
       alt="L5-G7_purple_logo" 
       width="150" 
       height="150" />
</p>

<h3 align="center">
  A project by Team L5-G7
</h3>

<p align="center">
    <img src="https://img.shields.io/badge/Java-17-blue.svg" alt="Java 17">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg" alt="Spring Boot">
    <img src="https://img.shields.io/badge/Database-H2%20/%20PostgreSQL-orange.svg" alt="Database">
</p>

## 📝 Description

**MealCraft** is a helper system for meal planning and grocery list management. It allows users to keep track of when they need to prepare their meals, while automatically generating shopping lists and calculating how long their prepared dishes will last.

## ✨ Key Features

* **👤 User Management**: Registration, authentication, and user profile management.
* **🥗 Recipe Management**: Create, edit, delete, and view recipes.
* **🛒 Product Management**: Add and organize products that serve as ingredients for recipes.
* **⚙️ Unit Management**: Ability to add and manage units of measurement (e.g., grams, liters, pieces).
* **🔔 Notifications**: A notification system for users.
* **🎲 Recipe Import**: Admins can import random recipes from an external API (`TheMealDB`).
* **🔐 Security**: JWT (JSON Web Token) based authorization to protect endpoints.

## 🛠️ Technologies

-   **Backend**: Java 17, Spring Boot 3.5.5
-   **Database**: H2 (for development), PostgreSQL (for production)
-   **Authentication**: Spring Security, JSON Web Tokens (JWT)
-   **ORM**: Spring Data JPA (Hibernate)
-   **Build Tools**: Gradle
-   **Helper Libraries**: Lombok, Jackson

## 🚀 Getting Started

To run the project locally, follow these steps.

### Prerequisites

-   Java (JDK) 17 or newer
-   Gradle 8.14.3 or newer

### Installation

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd MealCraft
    ```

2.  **Run the application:**
    Use the Gradle wrapper to run the application.
    ```bash
    ./gradlew bootRun
    ```
    Or for Windows:
    ```bash
    gradlew.bat bootRun
    ```
    The application will be running on `http://localhost:8080`.

### Configuration

-   **Database**: By default, the project uses an in-memory H2 database.
-   **H2 Console**: You can access the H2 console at: `http://localhost:8080/h2-console`.
    -   **JDBC URL**: `jdbc:h2:mem:testdb`
    -   **Username**: `sa`
    -   **Password**: `sa`
-   **Application Properties**: The main configuration is located in the `src/main/resources/application.properties` file.
