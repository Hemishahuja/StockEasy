# stock-easy

Boilerplate Spring Boot service

## Prerequisites

- Java 21
- Maven 3.9+

## Quick Start

To build and run the application:

```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`.

## Common Maven Commands

- **Clean and Verify (compile, test, package):**
  ```bash
  ./mvnw clean verify
  ```

- **Run Application:**
  ```bash
  ./mvnw spring-boot:run
  ```

- **Run Tests:**
  ```bash
  ./mvnw test
  ```

## Profiles

The application uses Spring profiles to manage configuration.

- `dev`: For local development. Activated with `-Pdev` or `SPRING_PROFILES_ACTIVE=dev`.
- `test`: For testing. Activated with `-Ptest` or `SPRING_PROFILES_ACTIVE=test`.
- `prod`: Default profile for production.

To run with a specific profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Docker

To build and run the application with Docker:

1.  **Build the Docker image:**
    ```bash
    docker build -t stock-easy .
    ```

2.  **Run the Docker container:**
    ```bash
    docker run -p 8080:8080 stock-easy
    ```