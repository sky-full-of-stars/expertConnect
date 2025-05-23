#!/bin/bash

# Kill any existing uvicorn processes
pkill -f "uvicorn"

# Source the secrets file
source ./secrets.sh

# Activate virtual environment
source src/main/java/com/uci/expertConnect/python_services/venv/bin/activate

# Start Redis if not running
if ! pgrep redis-server > /dev/null; then
    echo "Starting Redis server..."
    brew services start redis
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