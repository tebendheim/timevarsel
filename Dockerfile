# Use an official JDK runtime as a parent image
FROM openjdk:19-jdk-slim

# Create a directory for the app
WORKDIR /app

# Copy the gradle wrapper and configuration files
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts ./

# Make the gradlew script executable
RUN chmod +x gradlew

# Copy the rest of the application files
COPY src/ src/

# Run the build after copying the source files
RUN --mount=type=secret,id=gradle_props,target=~/app/gradle.properties

RUN ./gradlew build --no-daemon

# Verify that the jar file exists
RUN ls -l build/libs/

# Define the command to run the application
CMD ["sh", "-c", "java -jar build/libs/timevarsel-1.0.jar || sleep infinity"]

