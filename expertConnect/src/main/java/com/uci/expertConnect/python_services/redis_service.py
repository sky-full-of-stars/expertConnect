from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import redis
import json
from typing import List

app = FastAPI()

# Connect to Redis (adjust host/port if needed)
redis_client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)

# Request body schema
class ExpertStoreRequest(BaseModel):
    userId: str
    matchingExperts: List[int]

@app.post("/store-matching-experts")
def store_matching_experts(data: ExpertStoreRequest):
    try:
        redis_key = f"{data.userId}_last_retrieved_experts"
        expert_json = json.dumps(data.matchingExperts)
        redis_client.set(redis_key, expert_json)
        return {"message": f"Expert list stored for user {data.userId}"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/get-matching-experts/{userId}")
def get_matching_experts(userId: str):
    try:
        redis_key = f"{userId}_last_retrieved_experts"
        expert_json = redis_client.get(redis_key)
        if expert_json is None:
            raise HTTPException(status_code=404, detail="No expert list found for this user.")
        matching_experts = json.loads(expert_json)
        return {"userId": userId, "matchingExperts": matching_experts}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
