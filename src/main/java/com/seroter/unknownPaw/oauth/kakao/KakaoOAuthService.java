package com.seroter.unknownPaw.oauth.kakao;

import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Member.Role;
import com.seroter.unknownPaw.entity.Member.MemberStatus;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.security.util.JWTUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.transaction.annotation.Transactional;

// ✨ 로거 임포트 (선택 사항)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class KakaoOAuthService {

    // ✨ 로거 선언 (선택 사항)
    private static final Logger log = LoggerFactory.getLogger(KakaoOAuthService.class);

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    private final WebClient webClient;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;

    public KakaoOAuthService(WebClient.Builder webClientBuilder, MemberRepository memberRepository, JWTUtil jwtUtil) {
        this.webClient = webClientBuilder.build();
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    public Mono<KakaoTokenDTO> getKakaoAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoRestApiKey);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        log.info("카카오 Access Token 요청: code={}", code); // ✨ 로깅 추가
        return webClient.post()
                .uri(tokenUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(KakaoTokenDTO.class)
                .doOnError(e -> log.error("카카오 Access Token 발급 실패: {}", e.getMessage())); // ✨ 에러 로깅 추가
    }

    public Mono<KakaoUserDTO> getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        log.info("카카오 사용자 정보 요청: accessToken={}", accessToken.substring(0, 10) + "..."); // ✨ 로깅 추가 (토큰 전체 노출 주의)
        return webClient.get()
                .uri(userInfoUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(KakaoUserDTO.class)
                .doOnError(e -> log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage())); // ✨ 에러 로깅 추가
    }

    @Transactional
    public Mono<Member> kakaoLoginOrSignup(KakaoUserDTO kakaoUserDTO) {
        String socialId = String.valueOf(kakaoUserDTO.getId());
        String nickname = kakaoUserDTO.getProperties().getNickname();
        String profileImageUrl = kakaoUserDTO.getProperties().getProfileImage();

        String email = (kakaoUserDTO.getKakaoAccount() != null && kakaoUserDTO.getKakaoAccount().getEmail() != null)
                ? kakaoUserDTO.getKakaoAccount().getEmail()
                : "kakao_" + socialId + "@unknownpaw.com";

        log.info("카카오 로그인/회원가입 처리 시작: socialId={}", socialId); // ✨ 로깅 추가

        return Mono.fromCallable(() -> memberRepository.findBySocialIdAndSignupChannel(socialId, "KAKAO"))
                .flatMap(optionalMember -> {
                    Member member;
                    if (optionalMember.isPresent()) {
                        member = optionalMember.get();
                        log.info("기존 카카오 회원 로그인: {}", member.getNickname()); // ✨ 로깅 변경
                        // 닉네임, 프로필 이미지 경로 업데이트 (변경이 있을 때만 업데이트)
                        boolean changed = false;
                        if (!member.getNickname().equals(nickname)) {
                            member.setNickname(nickname);
                            changed = true;
                        }
                        if (profileImageUrl != null && !profileImageUrl.equals(member.getProfileImagePath())) {
                            member.setProfileImagePath(profileImageUrl);
                            changed = true;
                        }
                        if (changed) {
                            memberRepository.save(member); // 변경사항이 있을 때만 저장
                            log.info("기존 회원 정보 업데이트 완료: {}", member.getNickname());
                        }

                    } else {
                        // 새로운 회원 생성
                        member = Member.builder()
                                .email(email)
                                .password(null) // 소셜 로그인 시 비밀번호는 null
                                .fromSocial(true)
                                .socialId(socialId)
                                .name(nickname) // 실명은 닉네임으로 초기 설정
                                .nickname(nickname)
                                .phoneNumber(null)
                                .birthday(0)
                                .gender(null)    // ✨ Member 엔티티의 gender 필드가 Boolean 타입인지 확인해주세요!
                                .address(null)
                                .pawRate(0.0f)
                                .profileImagePath(profileImageUrl)
                                .emailVerified(kakaoUserDTO.getKakaoAccount() != null && kakaoUserDTO.getKakaoAccount().getIsEmailVerified() != null && kakaoUserDTO.getKakaoAccount().getIsEmailVerified())
                                .signupChannel("KAKAO")
                                .role(Role.USER) // ✨ Member 빌더에서 role 설정!
                                .status(MemberStatus.ACTIVE)
                                .build();

                        // ✨ 이 줄은 제거하는 것을 권장해요!
                        // member.addRole(Role.USER); // 이미 빌더에서 role을 설정했으므로 중복되거나 불필요할 수 있습니다.

                        log.info("새로운 카카오 회원 가입: {}", member.getNickname()); // ✨ 로깅 변경
                        memberRepository.save(member);
                    }
                    return Mono.just(member);
                });
    }
}