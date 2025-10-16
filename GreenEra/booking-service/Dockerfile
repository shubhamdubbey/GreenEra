# Use a small, optimized JDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file into image
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the port (each service will override via compose)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
