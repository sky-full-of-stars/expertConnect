#!/bin/bash

# Exit on error
set -e

echo "Setting up Python services..."

# Kill any existing uvicorn processes
pkill -f "uvicorn" || true

# Source the secrets file for OpenAI API key
source ./secrets.sh

# Check if Python virtual environment exists, if not create it
if [ ! -d "venv" ]; then
    echo "Creating Python virtual environment..."
    python3 -m venv venv
fi

# Activate virtual environment
echo "Activating virtual environment..."
source venv/bin/activate

# Install/upgrade pip
echo "Upgrading pip..."
pip install --upgrade pip

# Install all required packages
echo "Installing Python dependencies..."
pip install -r requirements.txt

# Check if Redis is installed
if ! command -v redis-server &> /dev/null; then
    echo "Redis server not found. Installing Redis..."
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        brew install redis
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        sudo apt-get update
        sudo apt-get install -y redis-server
    else
        echo "Unsupported OS for automatic Redis installation. Please install Redis manually."
        exit 1
    fi
fi

# Start Redis if not running
if ! pgrep redis-server > /dev/null; then
    echo "Starting Redis server..."
    redis-server &
    sleep 2  # Wait for Redis to start
fi

# Function to start a service
start_service() {
    local service_name=$1
    local port=$2
    echo "Starting $service_name on port $port..."
    cd src/main/java/com/uci/expertConnect/python_services
    uvicorn $service_name:app --port $port --host 0.0.0.0 --reload &
    cd ../../../../../../../
    sleep 2  # Wait for service to start
}

# Start all services
echo "Starting all Python services..."
start_service "chatting_service" 8001
start_service "generate_embedding_service" 8002
start_service "summarize_convo_service" 8003

echo "All services are running!"
echo "Chat Service: http://localhost:8001"
echo "Embedding Service: http://localhost:8002"
echo "Summarization Service: http://localhost:8003"
echo ""
echo "To stop all services, run: pkill -f uvicorn"
echo "To stop Redis server, run: redis-cli shutdown" 