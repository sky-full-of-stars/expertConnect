from fastapi import FastAPI
from pydantic import BaseModel
from openai import OpenAI
import redis
import json

OPENAI_API_KEY="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"

client = OpenAI(api_key=OPENAI_API_KEY)
app = FastAPI()

# Initialize Redis connection (adjust host/port if needed)
redis_client = redis.Redis(host="localhost", port=6379, db=0, decode_responses=True)

class UserRequest(BaseModel):
    userId: str

@app.post("/summarize-chat")
async def summarize_conversation(req: UserRequest):
    # Fetch chat history from Redis
    redis_key = f"chat:{req.userId}"
    chat_history_json = redis_client.get(redis_key)

    if not chat_history_json:
        return {"error": "No chat history found for the given userId."}

    try:
        messages = json.loads(chat_history_json)
    except json.JSONDecodeError:
        return {"error": "Failed to parse chat history from Redis."}

    # Build the prompt for summarization
    prompt = (
        "Summarize the user's query based on this conversation:\n\n" +
        "\n".join([f"{m['role']}: {m['content']}" for m in messages])
    )

    # Call OpenAI to summarize
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": prompt}]
    )

    summary = response.choices[0].message.content.strip()

    return {"summary": summary}