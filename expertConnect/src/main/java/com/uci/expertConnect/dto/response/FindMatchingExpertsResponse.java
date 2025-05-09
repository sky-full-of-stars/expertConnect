package com.uci.expertConnect.dto.response;

import com.uci.expertConnect.model.Expert;
import lombok.Data;
import java.util.List;

@Data
public class FindMatchingExpertsResponse {
    private List<Expert> experts;
}
