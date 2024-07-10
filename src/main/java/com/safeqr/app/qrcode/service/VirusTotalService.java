
package com.safeqr.app.qrcode.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safeqr.app.qrcode.dto.QRCodePayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class VirusTotalService {

    private static final Logger logger = LoggerFactory.getLogger(VirusTotalService.class);

    @Value("${virustotal.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public VirusTotalService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public String scanURL(QRCodePayload payload) {
        String urlToScan = payload.getData();
        logger.info("Scanning URL: {}", urlToScan);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://www.virustotal.com/api/v3/urls");

        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("content-type", "application/x-www-form-urlencoded");
        headers.set("x-apikey", apiKey);

        String body = "url=" + urlToScan;
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(builder.toUriString(), request, String.class);
        logger.info("Response from VirusTotal scan: {}", response.getBody());

        try {
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            return (String) data.get("id");
        } catch (Exception e) {
            logger.error("Error parsing response from VirusTotal scan", e);
            throw new RuntimeException("Error parsing response from VirusTotal scan", e);
        }
    }

    public boolean getAnalysis(String analysisId) {
        logger.info("Retrieving analysis for ID: {}", analysisId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://www.virustotal.com/api/v3/analyses/" + analysisId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("x-apikey", apiKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, String.class);
        logger.info("Response from VirusTotal analysis: {}", response.getBody());

        try {
            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            Map<String, Object> attributes = (Map<String, Object>) data.get("attributes");
            Map<String, Integer> stats = (Map<String, Integer>) attributes.get("stats");

            return evaluateSafety(stats);
        } catch (Exception e) {
            logger.error("Error parsing response from VirusTotal analysis", e);
            throw new RuntimeException("Error parsing response from VirusTotal analysis", e);
        }
    }

    private boolean evaluateSafety(Map<String, Integer> stats) {
        int malicious = stats.getOrDefault("malicious", 0);
        int suspicious = stats.getOrDefault("suspicious", 0);

        return malicious < 5 && suspicious < 5;
    }
}
