from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from openai import OpenAI
import os

app = FastAPI()

# OpenAI client with better error handling
api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    raise ValueError("OPENAI_API_KEY environment variable is not set")
client = OpenAI(api_key=api_key)

class ChatRequest(BaseModel):
    messages: list  # [{role: "user", content: "..."}]

@app.post("/summarize-chat")
async def summarize_chat(req: ChatRequest):
    # Build the prompt for summarization
    prompt = (
        "Summarize the user's query based on this conversation:\n\n" +
        "\n".join([f"{m['role']}: {m['content']}" for m in req.messages])
    )

    # Call OpenAI API to summarize
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": prompt}]
    )

    summary = response.choices[0].message.content.strip()

    return {"summary": summary}
