package com.auction.platform.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class ExternalApiService {
    public Double getUsdToEurRate() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.exchangerate-api.com/v4/latest/USD";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            Map<String, Double> rates = (Map<String, Double>) response.get("rates");
            return rates.get("EUR");
        } catch (Exception e) {
            return 0.92;
        }
    }
}