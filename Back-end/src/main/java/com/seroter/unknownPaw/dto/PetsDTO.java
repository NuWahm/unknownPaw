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
public class PetsDTO {
  private String petName;
  private String breed; // 견종
  private int petBirth;
  private boolean petGender;
  private double weight;
  private String petMbti; // 강아지 성격
  private boolean neutering; // 중성화 여부
  private String petIntroduce;

  @Builder.Default
  private Set<String> roleSet = new HashSet<>();
  private LocalDateTime regDate;
  private LocalDateTime modDate;
}
