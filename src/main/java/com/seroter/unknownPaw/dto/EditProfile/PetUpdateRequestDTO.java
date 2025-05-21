package com.seroter.unknownPaw.dto.EditProfile;

import lombok.*;

@Data // Lombok: Getter, Setter, EqualsAndHashCode, ToString 자동 생성
@NoArgsConstructor // Lombok: 기본 생성자 자동 생성
@AllArgsConstructor
public class PetUpdateRequestDTO {
  // 업데이트 가능한 필드들
  private String petName;
  private String breed;
  private int petBirth;
  private boolean petGender;
  private double weight;
  private String petMbti;
  private boolean neutering;
  private String petIntroduce;

  // 이미지 파일 업데이트를 위한 필드 (예시, 실제 파일 처리 방식에 따라 달라짐)
  // private MultipartFile imageFile;

  // Getter, Setter는 @Data 어노테이션으로 자동 생성됩니다.
}
