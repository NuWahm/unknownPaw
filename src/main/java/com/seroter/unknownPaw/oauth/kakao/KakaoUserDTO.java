package com.seroter.unknownPaw.oauth.kakao;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserDTO {
    private Long id; // 카카오 사용자 고유 ID (필수)
    @JsonProperty("connected_at")
    private String connectedAt;

    private Properties properties; // 사용자 프로필 정보

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount; // 카카오 계정 정보 (이메일 등)

    @Data
    public static class Properties {
        private String nickname; // 닉네임
        @JsonProperty("profile_image")
        private String profileImage; // 프로필 사진 URL
        @JsonProperty("thumbnail_image")
        private String thumbnailImage; // 썸네일 이미지 URL
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoAccount {
        @JsonProperty("profile_needs_agreement")
        private Boolean profileNeedsAgreement;

        @JsonProperty("profile_nickname_needs_agreement") // JSON 속성명과 정확히 일치시켜주세요.
        private Boolean profileNicknameNeedsAgreement; // 자바 필드명은 카멜케이스로!

        @JsonProperty("profile_image_needs_agreement")
        private Boolean profileImageNeedsAgreement;

        private Profile profile;

        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;
        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;
        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;
        private String email; // 이메일 주소 (현재 권한 없음 상태이므로 보통 null)
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Profile { // KakaoAccount 내의 프로필 정보
        private String nickname;
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;
        @JsonProperty("is_default_image")
        private Boolean isDefaultImage;
        @JsonProperty("is_default_nickname")
        private Boolean isDefaultNickname;
    }
}