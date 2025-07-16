#Step 1: Use base image with java
FROM openjdk:24-jdk-slim

#Step 2: Add working directory
WORKDIR /app

#Step 3: Copy the JAR into the Container
COPY target/EMS-JWT-0.0.1-SNAPSHOT.jar app.jar

#Step 4: Expose the port apps run on
EXPOSE 8080

#Step 5: Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]


