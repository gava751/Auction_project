# Этап 1: Сборка (Build)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
# Копируем сначала pom.xml для кэширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline
# Копируем исходный код и собираем JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Этап 2: Запуск (Run)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Копируем собранный JAR из первого этапа
COPY --from=builder /app/target/*.jar app.jar
# Открываем порт
EXPOSE 8080
# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]