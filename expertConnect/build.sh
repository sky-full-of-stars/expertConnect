#!/bin/bash

# Exit on error
set -e

echo "Starting build process..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "Docker is not running. Please start Docker and try again."
    exit 1
fi

# Create a Docker network if it doesn't exist
if ! docker network ls | grep -q expertconnect-network; then
    echo "Creating Docker network..."
    docker network create expertconnect-network
fi

# Create a volume for PostgreSQL data if it doesn't exist
if ! docker volume ls | grep -q expertconnect-postgres-data; then
    echo "Creating PostgreSQL data volume..."
    docker volume create expertconnect-postgres-data
fi

# Check if PostgreSQL container is running
if ! docker ps | grep -q expertconnect-postgres; then
    echo "Starting PostgreSQL container..."
    docker run --name expertconnect-postgres \
        --network expertconnect-network \
        -e POSTGRES_DB=expertconnect \
        -e POSTGRES_USER=akshay \
        -e POSTGRES_PASSWORD=postgres \
        -v expertconnect-postgres-data:/var/lib/postgresql/data \
        -p 5432:5432 \
        -d postgres:latest

    # Wait for PostgreSQL to be ready
    echo "Waiting for PostgreSQL to be ready..."
    sleep 10
fi

# Build the application
echo "Building the application..."
./mvnw clean package -DskipTests

echo "Build completed successfully!"

echo $JAVA_HOME
