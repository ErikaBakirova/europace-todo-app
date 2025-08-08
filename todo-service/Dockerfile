FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the jar file
COPY target/todo-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
