package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder

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
    private boolean gender;
    private String introduce;  // 소개
    private List<PetDTO> pets; // PetDTO 목록

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
        this.gender = member.getGender();
        this.introduce = member.getIntroduce();

        this.pets = member.getPets() == null ? null :
                member.getPets().stream()
                        .map(PetDTO::new)
                        .collect(Collectors.toList());
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
            boolean gender,
            String introduce,
            List<PetDTO> pets
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
        this.gender = gender;
        this.introduce = introduce;
        this.pets = pets;
    }
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Simple  {
        private Long mid;
        private String nickname;
        private float pawRate;
        private String profileImagePath;
    }
}

