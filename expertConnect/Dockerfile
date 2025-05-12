FROM ubuntu:22.04

# Avoid interactive prompts during python installation
ENV DEBIAN_FRONTEND=noninteractive

# Set working directory
WORKDIR /app

# Copy the Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src/ src/

# Build the application
RUN ./mvnw package -DskipTests

# Expose the application port
EXPOSE 8080

# Set the entry point
ENTRYPOINT ["java", "-jar", "target/expertConnect-0.0.1-SNAPSHOT.jar"] 
