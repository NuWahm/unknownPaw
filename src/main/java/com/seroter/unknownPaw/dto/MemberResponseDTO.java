package com.seroter.unknownPaw.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.seroter.unknownPaw.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;


@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberResponseDTO {
    private Long mid;
    private String email;
    private String nickname;
    private String profileImagePath;
    private float pawRate;
    private boolean emailVerified;
    private String role;
    private String status;
    private LocalDateTime regDate;    // 가입일
    private LocalDateTime modDate;    // 수정일
    private String phoneNumber;
    private String address;
    private boolean gender;
    private String introduce;         // 소개
    private List<PetDTO> pets;        // PetDTO 목록

    // **Member 엔티티로부터 변환 생성자**
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
        this.modDate = member.getModDate();         // <-- 수정일은 modDate로 따로!
        this.phoneNumber = member.getPhoneNumber();
        this.address = member.getAddress();
        this.gender = member.getGender();
        this.introduce = member.getIntroduce();
        this.pets = (member.getPets() == null) ? null :
                member.getPets().stream().map(PetDTO::new).collect(Collectors.toList());
    }

    // **필드 기반 생성자**
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
            String address,
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
        this.modDate = modDate;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.introduce = introduce;
        this.pets = pets;
    }

    // **간단 요약 DTO**
    @Getter
    @Builder
    @AllArgsConstructor
    public static class Simple {
        private Long mid;
        private String nickname;
        private float pawRate;
        private String profileImagePath;
    }
}