FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY target/payment-processor-0.0.1-SNAPSHOT.jar /app/payment-processor.jar

EXPOSE 8083

CMD ["java", "-jar", "payment-processor.jar"]
