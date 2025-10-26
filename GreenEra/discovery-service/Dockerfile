# Use a small, optimized JDK image
FROM openjdk:17-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the JAR file into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose Eureka's default port
EXPOSE 8761

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
