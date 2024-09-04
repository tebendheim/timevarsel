# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Enable BuildKit
RUN --mount=type=secret,id=gradle_props,dst=/root/.gradle/gradle.properties \
    ./gradlew build

# Set the working directory in the container
WORKDIR /app

# Copy the compiled JAR file into the container
COPY build/libs/your-application.jar app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]