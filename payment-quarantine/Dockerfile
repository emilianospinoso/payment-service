FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY target/payment-quarantine-0.0.1-SNAPSHOT.jar /app/payment-quarantine.jar

EXPOSE 8082

CMD ["java", "-jar", "payment-quarantine.jar"]
