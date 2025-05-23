package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Enum.Gender;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

