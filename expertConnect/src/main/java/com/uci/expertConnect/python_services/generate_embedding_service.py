from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import openai

# Your OpenAI API Key
OPENAI_API_KEY = "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"
openai.api_key = OPENAI_API_KEY
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
