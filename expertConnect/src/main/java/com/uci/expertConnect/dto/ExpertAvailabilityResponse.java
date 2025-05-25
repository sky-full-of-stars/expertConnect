package com.uci.expertConnect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpertAvailabilityResponse {
    private Map<String, List<TimeSlot>> available;
    private Map<String, List<TimeSlot>> not_available;
} 