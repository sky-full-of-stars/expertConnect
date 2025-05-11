from fastapi import FastAPI, Request
from pydantic import BaseModel
from typing import List
import redis
import openai
import json

app = FastAPI()
OPENAI_API_KEY="API_KEY"

# OpenAI client
client = openai.OpenAI(api_key=OPENAI_API_KEY)

# Redis client (default port 6379)
r = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)

class ChatRequest(BaseModel):
    userId: str
    message: str

class ChatResponse(BaseModel):
    reply: str

@app.post("/chat", response_model=ChatResponse)
def chat(req: ChatRequest):
    redis_key = f"chat:{req.userId}"

    # Get previous messages
    context_json = r.get(redis_key)
    if context_json:
        messages = json.loads(context_json)
    else:
        messages = [{"role": "system", "content": "You are a helpful assistant."}]

    # Add user's message
    messages.append({"role": "user", "content": req.message})

    # Call OpenAI
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=messages
    )
    reply = response.choices[0].message.content

    # Add assistant reply to context
    messages.append({"role": "assistant", "content": reply})

    # Save back to Redis
    r.set(redis_key, json.dumps(messages))

    return ChatResponse(reply=reply)
