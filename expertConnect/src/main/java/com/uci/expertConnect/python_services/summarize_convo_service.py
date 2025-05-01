from fastapi import FastAPI
from pydantic import BaseModel
from openai import OpenAI

client = OpenAI(api_key="API_KEY")  # replace with your actual key

app = FastAPI()

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
