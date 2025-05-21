package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MemberResponseDTO {
    private Long mid;                   // 회원 ID
    private String email;             // 이메일
    private String nickname;          // 닉네임
    private String profileImagePath;  // 프로필 이미지
    private float pawRate;            // 평점
    private boolean emailVerified;    // 이메일 인증 여부
    private String role;              // 역할
    private String status;            // 상태
    private LocalDateTime regDate;    // 가입일
    private LocalDateTime modDate;    // 가입일
    private String phoneNumber;
    private String address;


    public MemberResponseDTO(Member member) {
        this.mid = member.getMid();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.profileImagePath = member.getProfileImagePath();
        this.pawRate = member.getPawRate();
        this.emailVerified = member.isEmailVerified();
        this.role = member.getRole().name(); // enum → String
        this.status = member.getStatus().name(); // enum → String
        this.regDate = member.getRegDate();
        this.regDate = member.getModDate();
        this.phoneNumber = member.getPhoneNumber();
        this.address = member.getAddress();
    }

    public MemberResponseDTO(
            Long mid,
            String email,
            String nickname,
            String profileImagePath,
            float pawRate,
            boolean emailVerified,
            String role,
            String status,
            LocalDateTime regDate,
            LocalDateTime modDate,
            String phoneNumber,
            String address
    ) {
        this.mid = mid;
        this.email = email;
        this.nickname = nickname;
        this.profileImagePath = profileImagePath;
        this.pawRate = pawRate;
        this.emailVerified = emailVerified;
        this.role = role;
        this.status = status;
        this.regDate = regDate;
        this.modDate = modDate;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}

