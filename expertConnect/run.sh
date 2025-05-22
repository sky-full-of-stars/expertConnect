#!/bin/bash

# Exit on error
set -e

echo "Starting run process..."

# Make scripts executable
chmod +x build.sh
chmod +x secrets.sh
chmod +x start_services.sh

# Source secrets first
source ./secrets.sh

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

# Start Python services in the background
echo "Starting Python services..."
./start_services.sh &
PYTHON_SERVICES_PID=$!

# Run the application container
echo "Starting application container..."
docker run --name expertconnect-app \
    --network expertconnect-network \
    -p 8080:8080 \
    -e SPRING_DATASOURCE_URL="jdbc:postgresql://expertconnect-postgres:5432/expertconnect" \
    -e SPRING_DATASOURCE_USERNAME="akshay" \
    -e SPRING_DATASOURCE_PASSWORD="postgres" \
    -e ZOOM_API_KEY="zWgN2182QCC3tBLOFoOig" \
    -e ZOOM_API_SECRET="jW2pSyZNnY599w0CakjZEFbG9CA4GzUY" \
    -e ZOOM_API_USER_EMAIL="akshay.suryanarayan@gmail.com" \
    -e SPRING_MAIL_USERNAME="expertconnectforyou@gmail.com" \
    -e SPRING_MAIL_PASSWORD="qqkq ktxj irso qbkt" \
    -e AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID}" \
    -e AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY}" \
    -e AWS_REGION="${AWS_REGION}" \
    -e AWS_S3_BUCKET="${AWS_S3_BUCKET}" \
    -d expertconnect-app

echo "Application is starting up..."
echo "You can access the application at http://localhost:8080/expert-connect"
echo "To view the logs, run: docker logs -f expertconnect-app"
echo "To view the logs, run: docker logs -f expertconnect-app"

# Wait for user to press Ctrl+C
echo "Press Ctrl+C to stop the application..."
docker logs -f expertconnect-app

# Cleanup function to stop Python services when the script exits
cleanup() {
    echo "Stopping Python services..."
    kill $PYTHON_SERVICES_PID
    pkill -f "uvicorn"
    echo "Stopping application container..."
    docker stop expertconnect-app
    docker rm expertconnect-app
}
trap cleanup EXIT 