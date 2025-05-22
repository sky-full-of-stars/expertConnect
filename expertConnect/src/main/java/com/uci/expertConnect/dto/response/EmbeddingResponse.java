package com.uci.expertConnect.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmbeddingResponse {
    private List<Embedding> embeddings;  // List to hold the individual embeddings

    @Data
    public static class Embedding {
        private int id;  // ID of the text item
        private float[] embedding;  // The embedding vector
    }
}
