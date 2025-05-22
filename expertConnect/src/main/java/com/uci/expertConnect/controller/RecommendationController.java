package com.uci.expertConnect.controller;

import com.uci.expertConnect.dto.request.FindMatchingExpertsRequest;
import com.uci.expertConnect.service.ExpertMatchingService;
import com.uci.expertConnect.service.UserSearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/recommendation")
@RequiredArgsConstructor
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    private final UserSearchHistoryService userSearchHistoryService;
    private final ExpertMatchingService expertMatchingService;

    @GetMapping("/by-history/{userId}")
    public List<Integer> recommendExpertsFromHistory(@PathVariable String userId) {
        logger.info("Fetching recommended expert IDs for user: {}", userId);

        List<String> previousSearches = userSearchHistoryService.getSearchQueriesByUserId(userId);
        Set<Integer> recommendedExpertIds = new LinkedHashSet<>();

        for (String query : previousSearches) {
            logger.info("Processing historical query: {}", query);
            FindMatchingExpertsRequest request = new FindMatchingExpertsRequest();
            request.setUserId(userId);
            request.setText(query);

            List<Integer> matchedIds = expertMatchingService.findMatchingExperts(request);
            recommendedExpertIds.addAll(matchedIds);
        }

        // Shuffle and limit to 25
        // Logic and limit can be changed with different recommendation algorithms if necessary
        List<Integer> result = new ArrayList<>(recommendedExpertIds);
        Collections.shuffle(result);
        return result.stream().limit(25).toList();
    }
}
