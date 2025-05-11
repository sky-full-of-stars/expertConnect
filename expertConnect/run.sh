#!/bin/bash

# Exit on error
set -e

echo "Starting run process..."

# Make scripts executable
chmod +x build.sh

# Run the build script
./build.sh

# Build the Docker image
echo "Building Docker image..."
docker build -t expertconnect-app .

# Check if the application container is already running
if docker ps | grep -q expertconnect-app; then
    echo "Stopping existing application container..."
    docker stop expertconnect-app
    docker rm expertconnect-app
fi

# Run the application container
echo "Starting application container..."
docker run --name expertconnect-app \
    --network expertconnect-network \
    -p 8080:8080 \
    -e SPRING_DATASOURCE_URL=jdbc:postgresql://expertconnect-postgres:5432/expertconnect \
    -e SPRING_DATASOURCE_USERNAME=akshay \
    -e SPRING_DATASOURCE_PASSWORD=postgres \
    -e ZOOM_API_KEY=zWgN2182QCC3tBLOFoOig \
    -e ZOOM_API_SECRET=jW2pSyZNnY599w0CakjZEFbG9CA4GzUY \
    -e ZOOM_API_USER_EMAIL=akshay.suryanarayan@gmail.com \
    -e SPRING_MAIL_USERNAME=expertconnectforyou@gmail.com \
    -e SPRING_MAIL_PASSWORD=qqkq\ ktxj\ irso\ qbkt \
    -d expertconnect-app

echo "Application is starting up..."
echo "You can access the application at http://localhost:8080/expert-connect"
echo "To view the logs, run: docker logs -f expertconnect-app" 