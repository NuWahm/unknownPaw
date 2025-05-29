package com.seroter.unknownPaw.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.seroter.unknownPaw.dto.KakaoMapsDTO.GeocodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;

@Service
@Slf4j
public class KakaoMapsService {

    private final WebClient webClient;
    private final Cache<String, GeocodeResponse> geocodeCache;
    private final String apiKey;

    public KakaoMapsService(
            @Value("${kakao.maps.api-url}") String apiUrl,
            @Value("${kakao.maps.rest-api-key}") String apiKey  // api-key -> rest-api-key로 변경
    ) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();

        this.geocodeCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(24, TimeUnit.HOURS)
                .build();
    }


    public GeocodeResponse geocode(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("주소가 비어있습니다.");
        }

        return geocodeCache.get(address, key -> {
            try {
                log.info("Geocoding request for address: {}", key);

                GeocodeResponse response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v2/local/search/address.json")
                                .queryParam("query", key)
                                .build())
                        .header("Authorization", "KakaoAK " + apiKey)
                        .retrieve()
                        .onStatus(status -> status.is4xxClientError(),
                                clientResponse -> Mono.error(new RuntimeException("카카오 API 호출 실패: " + clientResponse.statusCode())))
                        .onStatus(status -> status.is5xxServerError(),
                                clientResponse -> Mono.error(new RuntimeException("카카오 API 서버 오류: " + clientResponse.statusCode())))
                        .bodyToMono(GeocodeResponse.class)
                        .timeout(Duration.ofSeconds(10))
                        .block();

                if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
                    log.warn("No results found for address: {}", key);
                    throw new RuntimeException("주소를 찾을 수 없습니다.");
                }

                log.info("Geocoding successful for address: {}", key);
                return response;
            } catch (WebClientResponseException e) {
                log.error("Geocoding failed for address: {} with status: {}", key, e.getStatusCode(), e);
                throw new RuntimeException("주소 변환에 실패했습니다: " + e.getStatusCode());
            } catch (Exception e) {
                log.error("Unexpected error during geocoding for address: {}", key, e);
                throw new RuntimeException("주소 변환 중 예기치 않은 오류가 발생했습니다: " + e.getMessage());
            }
        });
    }

    public Map<String, Double> getCoordinates(String address) {
        GeocodeResponse response = geocode(address);
        if (response.getDocuments() == null || response.getDocuments().isEmpty()) {
            throw new RuntimeException("주소를 찾을 수 없습니다.");
        }

        GeocodeResponse.Document document = response.getDocuments().get(0);
        Map<String, Double> coordinates = new HashMap<>();
        coordinates.put("latitude", Double.parseDouble(document.getY()));
        coordinates.put("longitude", Double.parseDouble(document.getX()));

        return coordinates;
    }
} 