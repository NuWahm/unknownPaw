package com.seroter.unknownPaw.oauth.kakao;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class KakaoTokenDTO {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    private String scope;

    @JsonProperty("refresh_token_expires_in")
    private Integer refreshTokenExpiresIn;
}