package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.ErrorResponse;
import com.seroter.unknownPaw.dto.KakaoMapsDTO.GeocodeResponse;
import com.seroter.unknownPaw.service.KakaoMapsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/maps")
@Slf4j
@RequiredArgsConstructor
public class KakaoMapsController {
    private final KakaoMapsService kakaoMapsService;

    @GetMapping("/geocode")
    public ResponseEntity<?> geocode(@RequestParam String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "주소가 비어있습니다."));
            }

            GeocodeResponse response = kakaoMapsService.geocode(query);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid address parameter", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Geocoding error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during geocoding", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "주소 변환 중 예기치 않은 오류가 발생했습니다."));
        }
    }

    @GetMapping("/coordinates")
    public ResponseEntity<?> getCoordinates(@RequestParam String address) {
        try {
            Map<String, Double> coordinates = kakaoMapsService.getCoordinates(address);
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            log.error("Coordinates error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("좌표 변환에 실패했습니다: " + e.getMessage()));
        }
    }
}