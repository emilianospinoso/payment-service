# Use a base image with Java
FROM openjdk:17-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/payment-quarantine-0.0.1-SNAPSHOT.jar /app/payment-quarantine.jar

# Expose the application port
EXPOSE 8082

# Command to run the application
CMD ["java", "-jar", "payment-quarantine.jar"]
