package com.nab.voucher.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProvisionServiceClient {

    @Value("${api.voucher.provision-service-url}")
    String provisionServiceUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ProvisionServiceClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getVoucher(String telco, int value) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("telco", telco);
        requestBody.put("value", value);
        try {
            String response = restTemplate.postForObject(provisionServiceUrl, requestBody, String.class);
            Map<?, ?> responseData = objectMapper.readValue(response, Map.class);
            return (String) responseData.get("code");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing provision response", e);
        }
    }
}
