from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import openai
import os

app = FastAPI()

# OpenAI client with better error handling
api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    raise ValueError("OPENAI_API_KEY environment variable is not set")
client = openai.OpenAI(api_key=api_key)

class TextItem(BaseModel):
    id: int
    text: str

class EmbeddingRequest(BaseModel):
    items: List[TextItem]  # List of items with id and text

@app.post("/generate-embedding")
async def generate_embedding(req: EmbeddingRequest):
    # Generate embeddings for all texts in the list
    response = client.embeddings.create(
        model="text-embedding-ada-002",
        input=[item.text for item in req.items]
    )
    
    # Extract embeddings and pair them with their ids
    embeddings = [
        {"id": req.items[i].id, "embedding": response.data[i].embedding}  # Use `.data` and `.embedding`
        for i in range(len(req.items))
    ]
    
    return {"embeddings": embeddings}
