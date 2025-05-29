package com.seroter.unknownPaw.oauth.kakao;

import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.security.util.JWTUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/oauth") // ✨ 컨텍스트 패스(unknownPaw)는 자동 적용되므로 여기에 포함하지 않아요.
public class KakaoOAuthController {

    private static final Logger log = LoggerFactory.getLogger(KakaoOAuthController.class);

    private final KakaoOAuthService kakaoOAuthService;
    private final JWTUtil jwtUtil;

    public KakaoOAuthController(KakaoOAuthService kakaoOAuthService, JWTUtil jwtUtil) {
        this.kakaoOAuthService = kakaoOAuthService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/kakao") // ✨ 최종적으로 /unknownPaw/api/oauth/kakao 경로가 됩니다.
    public Mono<ResponseEntity<Map<String, Object>>> kakaoLogin(@RequestParam("code") String code) {
        log.info("백엔드: 카카오 인가 코드 수신: {}", code);

        return kakaoOAuthService.getKakaoAccessToken(code)
                .flatMap(tokenDTO -> {
                    if (tokenDTO == null || tokenDTO.getAccessToken() == null) {
                        log.warn("백엔드: 카카오 Access Token 발급 실패 (토큰 없음).");
                        return Mono.just(ResponseEntity.badRequest().body(createErrorResponse("Failed to get Kakao access token.")));
                    }
                    String accessToken = tokenDTO.getAccessToken();
                    log.info("백엔드: 카카오 Access Token: {}", accessToken.substring(0, Math.min(accessToken.length(), 10)) + "..."); // 10자까지만 표시

                    return kakaoOAuthService.getKakaoUserInfo(accessToken)
                            .flatMap(userDTO -> {
                                if (userDTO == null || userDTO.getId() == null) {
                                    log.warn("백엔드: 카카오 사용자 정보 조회 실패 (ID 없음).");
                                    return Mono.just(ResponseEntity.badRequest().body(createErrorResponse("Failed to get Kakao user info or user ID is null.")));
                                }
                                log.info("백엔드: 카카오에서 받은 사용자 정보: ID={}, 닉네임={}", userDTO.getId(), userDTO.getProperties().getNickname());

                                return kakaoOAuthService.kakaoLoginOrSignup(userDTO)
                                        .map(member -> {
                                            log.info("백엔드: 회원 로그인/회원가입 처리 성공: MID={}, 닉네임={}", member.getMid(), member.getNickname());
                                            String email = member.getEmail(); // 멤버에서 이메일 가져오기
                                            String roleString = member.getRole().name(); // 멤버에서 역할 가져오기

                                            String jwtToken = jwtUtil.generateToken(email, roleString);

                                            Map<String, Object> responseBody = new HashMap<>();
                                            responseBody.put("message", "Kakao login successful!");
                                            responseBody.put("token", jwtToken);
                                            responseBody.put("memberInfo", Map.of(
                                                    "mid", member.getMid(),
                                                    "nickname", member.getNickname(),
                                                    "profileImagePath", member.getProfileImagePath(),
                                                    "role", member.getRole().name()
                                            ));
                                            return ResponseEntity.ok(responseBody);
                                        })
                                        .doOnError(e -> log.error("백엔드: 카카오 사용자 처리 중 오류 발생: {}", e.getMessage(), e))
                                        .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Error processing Kakao user: " + e.getMessage()))));
                            })
                            .doOnError(e -> log.error("백엔드: 카카오 사용자 정보 조회 중 오류 발생: {}", e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Error getting Kakao user info: " + e.getMessage()))));
                })
                .doOnError(e -> log.error("백엔드: 카카오 Access Token 획득 중 오류 발생: {}", e.getMessage(), e))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse("Error getting Kakao access token: " + e.getMessage()))));
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return errorResponse;
    }
}