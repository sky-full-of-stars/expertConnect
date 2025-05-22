package com.uci.expertConnect.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.uci.expertConnect.dto.request.FindMatchingExpertsRequest;
import com.uci.expertConnect.repository.ExpertRepository;
import com.uci.expertConnect.repository.UserSearchHistoryRepository;
import com.uci.expertConnect.service.ExpertMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.uci.expertConnect.model.UserSearchHistory;



@Service
public class ExpertMatchingServiceImpl implements ExpertMatchingService {

    private final ExpertRepository expertRepository;
    private final RestTemplate restTemplate;

    @Value("${embedding.service.url}")
    private String embeddingServiceUrl; // e.g., http://localhost:8002/generate_embedding

    public ExpertMatchingServiceImpl(ExpertRepository expertRepository, RestTemplate restTemplate) {
        this.expertRepository = expertRepository;
        this.restTemplate = restTemplate;
    }

    @Autowired
    private UserSearchHistoryRepository searchHistoryRepository;

    @Override
    public List<Integer> findMatchingExperts(FindMatchingExpertsRequest request) {
        String userId = request.getUserId();
        String query = request.getText();

        UserSearchHistory history = new UserSearchHistory();
        history.setUserId(userId);
        history.setQuery(query);
        searchHistoryRepository.save(history);

        // Call the embedding service
        float[] queryEmbedding = getEmbeddingOfQuery(request.getText());
        if (queryEmbedding == null || queryEmbedding.length == 0) {
            return Collections.emptyList();
        }

        //Query Postgres for top 50 experts by cosine similarity
        return expertRepository.findTop50ByEmbedding(queryEmbedding);
    }


    private float[] getEmbeddingOfQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query text must not be null or empty.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construct the request body
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("text", query);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("items", Collections.singletonList(item));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Send the POST request and get the response as JsonNode
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                embeddingServiceUrl,
                request,
                JsonNode.class
        );

        JsonNode embeddingsNode = response.getBody().get("embeddings");
        if (embeddingsNode != null && embeddingsNode.isArray() && embeddingsNode.size() > 0) {
            JsonNode embeddingNode = embeddingsNode.get(0).get("embedding");
            if (embeddingNode != null && embeddingNode.isArray()) {
                float[] embeddings = new float[embeddingNode.size()];
                for (int i = 0; i < embeddingNode.size(); i++) {
                    embeddings[i] = embeddingNode.get(i).floatValue();
                }
                return embeddings;
            }
        }

        return new float[0]; // Return an empty array if the embedding is not found
    }
}
