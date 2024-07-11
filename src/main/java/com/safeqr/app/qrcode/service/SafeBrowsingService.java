package com.safeqr.app.qrcode.service;

import com.safeqr.app.qrcode.dto.SafeBrowsingRequest;
import com.safeqr.app.qrcode.dto.SafeBrowsingResponse;
import com.safeqr.app.qrcode.entity.SafeBrowsingCache;
import com.safeqr.app.qrcode.repository.SafeBrowsingCacheRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SafeBrowsingService {

    @Value("${google.safebrowsing.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final SafeBrowsingCacheRepository cacheRepository;

    public SafeBrowsingService(WebClient.Builder webClientBuilder, SafeBrowsingCacheRepository cacheRepository) {
        this.webClient = webClientBuilder.baseUrl("https://safebrowsing.googleapis.com/v4/threatMatches:find").build();
        this.cacheRepository = cacheRepository;
    }

    @PostConstruct
    public void initializeCache() {
        // Fetch the full list of hashes from Google and store them in the local database.
        // This is a placeholder method. You need to implement the logic to fetch and store the hashes.
        fetchAndStoreFullHashes();
    }

    public Mono<Boolean> isSafeUrl(String url) throws NoSuchAlgorithmException {
        String hashPrefix = getHashPrefix(url);

        Optional<SafeBrowsingCache> cachedResult = cacheRepository.findByHashPrefix(hashPrefix);
        if (cachedResult.isPresent()) {
            return Mono.just(cachedResult.get().getFullHash().isEmpty());
        }

        // If not in cache, call Google Safe Browsing API
        String requestUrl = "?key=" + apiKey;

        SafeBrowsingRequest request = new SafeBrowsingRequest(
                new SafeBrowsingRequest.Client("safeqr-fyp-24", "1.0"),
                new SafeBrowsingRequest.ThreatInfo(
                        List.of("MALWARE", "SOCIAL_ENGINEERING"),
                        List.of("WINDOWS"),
                        List.of("URL"),
                        List.of(new SafeBrowsingRequest.ThreatInfo.ThreatEntry(url))
                )
        );

        return webClient.post()
                .uri(requestUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SafeBrowsingResponse.class)
                .map(response -> {
                    boolean isSafe = response.getMatches() == null || response.getMatches().isEmpty();
                    if (!isSafe) {
                        SafeBrowsingResponse.ThreatMatch match = response.getMatches().get(0);
                        SafeBrowsingCache cache = new SafeBrowsingCache();
                        cache.setId(UUID.randomUUID());
                        cache.setHashPrefix(hashPrefix);
                        cache.setThreatType(match.getThreatType());
                        cache.setPlatformType(match.getPlatformType());
                        cache.setThreatEntryType(match.getThreatEntryType());
                        cache.setFullHash(match.getThreat().getHash());
                        cacheRepository.save(cache);
                    }
                    return isSafe;
                });
    }

    private String getHashPrefix(String url) throws NoSuchAlgorithmException {
        // Compute hash prefix of the URL (first 4 bytes of SHA-256)
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(url.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash).substring(0, 4);
    }

    private void fetchAndStoreFullHashes() {
        // Implement the logic to fetch and store the full list of hashes from Google Safe Browsing API
        // This could involve using the threatListUpdates:fetch endpoint
    }
}