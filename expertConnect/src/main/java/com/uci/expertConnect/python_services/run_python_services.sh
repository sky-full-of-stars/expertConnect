#!/bin/bash

# Run each uvicorn service in the background
uvicorn chatting_service:app --port 8000 &
uvicorn summarize_convo_service:app --port 8001 &
uvicorn generate_embedding_service:app --port 8002 &
uvicorn redis_service:app --port 8003 &

# Wait for all background jobs to keep the script running
wait