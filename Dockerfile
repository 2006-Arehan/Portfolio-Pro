# Stage 1: Build the JAR with Maven + Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run the JAR
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/portfoliopro-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 7860
# Shell form so $PORT env var is expanded correctly at runtime
CMD ["sh", "-c", "java -Dserver.port=${PORT:-7860} -jar app.jar"]
