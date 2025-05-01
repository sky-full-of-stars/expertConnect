from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import openai

# Your OpenAI API Key
openai.api_key = "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"

app = FastAPI()

class TextItem(BaseModel):
    id: int
    text: str

class EmbeddingRequest(BaseModel):
    items: List[TextItem]  # List of items with id and text

@app.post("/generate-embedding")
async def generate_embedding(req: EmbeddingRequest):
    # Generate embeddings for all texts in the list
    response = openai.embeddings.create(
        model="text-embedding-ada-002",
        input=[item.text for item in req.items]
    )
    
    # Extract embeddings and pair them with their ids
    embeddings = [
        {"id": req.items[i].id, "embedding": response.data[i].embedding}  # Use `.data` and `.embedding`
        for i in range(len(req.items))
    ]
    
    return {"embeddings": embeddings}
