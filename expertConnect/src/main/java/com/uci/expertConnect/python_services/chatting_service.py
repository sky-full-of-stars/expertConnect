from fastapi import FastAPI, Request, HTTPException
from pydantic import BaseModel
from typing import List
import redis
import openai
import json
import os

app = FastAPI()

# OpenAI client with better error handling
api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    raise ValueError("OPENAI_API_KEY environment variable is not set")
client = openai.OpenAI(api_key=api_key)

# Redis client (default port 6379)
r = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)

class ChatRequest(BaseModel):
    userId: str
    message: str

class ChatResponse(BaseModel):
    reply: str
    retrieveProfiles: bool

@app.post("/chat", response_model=ChatResponse)
def chat(req: ChatRequest):
    redis_key = f"chat:{req.userId}"

    # Load previous conversation
    context_json = r.get(redis_key)
    if context_json:
        messages = json.loads(context_json)
    else:
        messages = [{
            "role": "system",
            "content": (
                "You are a helpful assistant. Your job is to help users by chatting with them, understanding their needs, and offering to connect them with relevant experts only when appropriate.\n\n"
                "Always respond in the following JSON format:\n"
                "{\n"
                '  "reply": "<your natural language response>",\n'
                '  "retrieveProfiles": <true or false>\n'
                "}\n\n"

                "Guidelines:\n"
                "1. If the user's request is vague (e.g., 'I need help'), ask a follow-up question to gather more details. Do NOT set retrieveProfiles to true yet.\n"
                "2. If you understand the user's issue clearly, you may ask: 'Would you like me to connect you with experts who can help?'. Still, set retrieveProfiles to false until the user agrees.\n"
                "3. ONLY set retrieveProfiles to true when the user explicitly confirms interest (e.g., replies with 'yes', 'that would be helpful', etc.) after you asked about connecting with experts.\n\n"

                "Keep all responses short, polite, and in helpful tone. Be sure to follow the format strictly."
            )
        }]

    messages.append({"role": "user", "content": req.message})

    # Call OpenAI
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=messages
    )

    # Extract raw content
    raw_reply = response.choices[0].message.content.strip()

    try:
        parsed = json.loads(raw_reply)
        reply = parsed.get("reply", "")
        retrieveProfiles = parsed.get("retrieveProfiles", False)
    except Exception:
        # fallback if not parsable
        reply = raw_reply
        retrieveProfiles = False

    # Add assistant reply to context
    messages.append({"role": "assistant", "content": raw_reply})
    r.setex(redis_key, 600, json.dumps(messages))  # 600 seconds = 10 minutes

    return ChatResponse(reply=reply, retrieveProfiles=retrieveProfiles)
