# Stage 1: Build the application
FROM eclipse-temurin:21-jdk as builder
WORKDIR /workspace
COPY . .
RUN ./mvnw -B package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]