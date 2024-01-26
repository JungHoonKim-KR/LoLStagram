package com.example.reactmapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Convert
@RequiredArgsConstructor
public class StringListConverter implements AttributeConverter<List<String>,String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try{
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try{
            return objectMapper.readValue(dbData,List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
