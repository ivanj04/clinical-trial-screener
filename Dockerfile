## Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn package -DskipTests -q

## Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S ctscreener && adduser -S ctscreener -G ctscreener
COPY --from=builder /app/target/ctscreener-*.jar app.jar
USER ctscreener
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
