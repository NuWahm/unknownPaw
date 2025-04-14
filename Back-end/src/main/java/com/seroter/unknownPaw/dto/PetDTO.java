package com.seroter.unknownPaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetDTO {
  private Long petId; // 펫 고유 번호(PK)
  private String petName; // 펫 이름
  private String breed; // 견종
  private int petBirth; // 펫 출생 연도(예: 2025)
  private boolean petGender; // 펫 성별
  private double weight; // 무게
  private String petMbti; // 펫 성격
  private boolean neutering; // 중성화 여부
  private String petIntroduce; // 펫 소개

  @Builder.Default
  private Set<String> roleSet = new HashSet<>();
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
