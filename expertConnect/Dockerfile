FROM python:3.11-slim

# Install OpenJDK 17 (Temurin build)
RUN apt-get update && \
    apt-get install -y wget gnupg && \
    wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor -o /usr/share/keyrings/adoptium.gpg && \
    echo "deb [signed-by=/usr/share/keyrings/adoptium.gpg] https://packages.adoptium.net/artifactory/deb bookworm main" > /etc/apt/sources.list.d/adoptium.list && \
    apt-get update && \
    apt-get install -y temurin-17-jdk && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Confirm installations
RUN java -version && python --version

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
