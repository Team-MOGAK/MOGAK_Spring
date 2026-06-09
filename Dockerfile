FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src
COPY sql ./sql

RUN sh gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

RUN addgroup -S mogak && adduser -S mogak -G mogak

COPY --from=builder /workspace/build/libs/*.jar app.jar

USER mogak
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
