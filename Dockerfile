FROM mcr.microsoft.com/playwright/java:v1.44.0-jammy

WORKDIR /app

COPY target/QA-AI-Agent-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]