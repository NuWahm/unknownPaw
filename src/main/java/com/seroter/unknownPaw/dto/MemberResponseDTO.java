package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    // Member 엔티티를 기반으로 DTO 변환
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
        this.introduce = member.getIntroduce(); // 소개 처리

        // 펫 목록 처리
        this.pets = member.getPets() == null ? null : member.getPets().stream()
                .map(pet -> new PetDTO(pet)) // PetDTO로 변환
                .collect(Collectors.toList());
    }


}
