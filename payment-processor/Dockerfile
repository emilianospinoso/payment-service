# Use a base image with Java 21
FROM openjdk:17-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/payment-processor-0.0.1-SNAPSHOT.jar /app/payment-processor.jar

# Expose the application port
EXPOSE 8083

# Command to run the application
CMD ["java", "-jar", "payment-processor.jar"]
