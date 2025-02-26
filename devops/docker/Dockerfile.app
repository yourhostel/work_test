# Dockerfile.app

# Використовуємо JDK 21
FROM eclipse-temurin:21-jdk

# Встановлюємо робочу директорію в контейнері
WORKDIR /app

# Копіюємо JAR-файл застосунку
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Визначаємо змінні середовища для підключення до PostgreSQL (будуть перевизначені в K3s)
ENV DATABASE_URL=jdbc:postgresql://postgres-service:5432/work_db
ENV DATABASE_USER=postgres
ENV DATABASE_PASSWORD=postgres

# Запускаємо Spring Boot застосунок
ENTRYPOINT ["java", "-jar", "app.jar"]
