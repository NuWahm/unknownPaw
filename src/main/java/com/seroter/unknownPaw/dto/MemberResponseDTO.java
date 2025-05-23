package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Enum.Gender;
import com.seroter.unknownPaw.entity.Member;
import lombok.Builder;
import lombok.Getter;

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
        this.role = member.getRole().name(); // enum → String
        this.status = member.getStatus().name(); // enum → String
        this.regDate = member.getRegDate();
        this.gender = member.getGender();
        this.introduce = member.getIntroduce(); // 소개 처리

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
            LocalDateTime regDate
            boolean gender,
            String introduce
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
        this.gender = isGender();
        this.introduce = introduce();

        // 펫 목록 처리
        this.pets = member.getPets() == null ? null : member.getPets().stream()
                .map(pet -> new PetDTO(pet)) // PetDTO로 변환
                .collect(Collectors.toList());
    }
}

