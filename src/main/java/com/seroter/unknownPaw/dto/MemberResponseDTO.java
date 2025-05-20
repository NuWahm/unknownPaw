package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class MemberResponseDTO {
    private Long mid;
    private String email;
    private String nickname;
    private String profileImagePath;
    private float pawRate;
    private boolean emailVerified;
    private String role;
    private String status;
    private LocalDateTime regDate;

    public MemberResponseDTO(Member member) {
        this.mid = member.getMid();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.profileImagePath = member.getProfileImagePath();
        this.pawRate = member.getPawRate();
        this.emailVerified = member.isEmailVerified();
        this.role = member.getRole().name();
        this.status = member.getStatus().name();
        this.regDate = member.getRegDate();
    }

    public MemberResponseDTO(Long mid, String email, String nickname, String profileImagePath, float pawRate,
                             boolean emailVerified, String role, String status, LocalDateTime regDate) {
        this.mid = mid;
        this.email = email;
        this.nickname = nickname;
        this.profileImagePath = profileImagePath;
        this.pawRate = pawRate;
        this.emailVerified = emailVerified;
        this.role = role;
        this.status = status;
        this.regDate = regDate;
    }
}
