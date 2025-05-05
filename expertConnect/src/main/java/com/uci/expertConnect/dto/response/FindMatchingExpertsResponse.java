package com.uci.expertConnect.dto.response;

import com.uci.expertConnect.model.Expert;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FindMatchingExpertsResponse {
    private List<Expert> experts;
}
