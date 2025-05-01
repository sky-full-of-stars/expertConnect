package com.uci.expertConnect.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class EmbeddingRequest {

    @NotNull(message = "Items are required")
    private List<Item> items;  // List of items (with id and text)

    @Data
    @AllArgsConstructor
    public static class Item {
        private int id;       // ID of the text (in this case, we'll pass 1 for the bio)
        @NotNull(message = "Text is required")
        private String text;  // Bio text
    }
}
