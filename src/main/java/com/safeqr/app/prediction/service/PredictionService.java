package com.safeqr.app.prediction.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safeqr.app.prediction.model.URLFeaturesMapper;
import com.safeqr.app.qrcode.model.URLModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import static com.safeqr.app.constants.APIConstants.PREDICTION_API_URL;

@Service
public class PredictionService {
    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public PredictionService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String predict(URLModel urlModel) {
        // Convert URLModel to URLFeatures
        URLFeaturesMapper features = URLFeaturesMapper.fromEntity(urlModel);
        logger.info("Prediction request: {}", features);
        logger.info("feature contents : {}", features.getContents());
        logger.info("feature domain : {}", features.getDomain());
        logger.info("feature sub-domain : {}", features.getSubdomain());
        logger.info("feature tld : {}", features.getTopLevelDomain());
        logger.info("feature path : {}", features.getPath());
        logger.info("feature query : {}", features.getQuery());
        logger.info("feature fragment : {}", features.getFragment());
        logger.info("feature redirect : {}", features.getRedirect());
        logger.info("feature redirect chain: {}", features.getRedirectChain());
        logger.info("feature shortening service: {}", features.getShorteningService());
        logger.info("feature hasExecutable: {}", features.getHasExecutable());
        logger.info("feature hasIP: {}", features.getHasIpAddress());
        logger.info("feature hostname embedding: {}", features.getHostnameEmbedding());
        logger.info("feature hsts header: {}", features.getHstsHeader());
        logger.info("feature javascript check: {}", features.getJavascriptCheck());
        logger.info("feature tracking: {}", features.getTrackingDescriptions());
        logger.info("feature urlencoding: {}", features.getUrlEncoding());

        // Prepare the HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity containing the features and headers
        HttpEntity<URLFeaturesMapper> requestEntity = new HttpEntity<>(features, headers);

        // Make the HTTP POST request to the FastAPI prediction endpoint
        ResponseEntity<String> response = restTemplate.exchange(
                PREDICTION_API_URL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Use ObjectMapper to deserialize the response and automatically remove quotes
        String prediction = response.getBody();
        try {
            prediction = objectMapper.readValue(prediction, String.class);
        } catch (Exception e) {
            logger.error("Failed to parse prediction response", e);
            prediction = "Unknown";
        }
        logger.info("Prediction response: {}", prediction);

        // Return the prediction
        return prediction;
    }
}
