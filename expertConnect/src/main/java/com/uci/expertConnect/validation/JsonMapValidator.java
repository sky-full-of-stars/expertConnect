package com.uci.expertConnect.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.List;

public class JsonMapValidator implements ConstraintValidator<ValidJsonMap, Map<String, List<String>>> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean isValid(Map<String, List<String>> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle this
        }
        
        try {
            // Try to convert the map to JSON string
            objectMapper.writeValueAsString(value);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
} 