package com.seroter.unknownPaw.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequestDto {
    private String email;           // 이메일
    private String password;        // 비밀번호 (일반 로그인일 경우)
    private String name;            // 이름
    private String nickname;        // 닉네임
    private String phoneNumber;     // 전화번호
    private int birthday;           // 생년
    private Boolean gender;         // 성별
    private String address;         // 주소
    private boolean fromSocial;     // 소셜 여부
    private String socialId;        // 소셜 ID
    private String signupChannel;   // 가입 경로
}