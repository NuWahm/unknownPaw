package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Pet;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetDTO {

  private Long petId;
  private String petName;
  private String breed;
  private int petBirth;
  private boolean petGender;
  private double weight;
  private String petMbti;
  private boolean neutering;
  private String petIntroduce;
  private String status;
  private LocalDateTime regDate;
  private LocalDateTime modDate;

  private Long mid;         // 회원 ID (foreign key 역할)
  private String imagePath; // 이미지 경로
  private String thumbnailPath;


  /**
   * Entity → DTO 변환 팩토리 메서드
   */
  public static PetDTO fromEntity(Pet pet) {
    if (pet == null) return null;
    return PetDTO.builder()
        .petId(pet.getPetId())
        .petName(pet.getPetName())
        .breed(pet.getBreed())
        .petBirth(pet.getPetBirth())
        .petGender(pet.isPetGender())
        .weight(pet.getWeight())
        .petMbti(pet.getPetMbti())
        .neutering(pet.isNeutering())
        .petIntroduce(pet.getPetIntroduce())
        .status(pet.getStatus() != null ? pet.getStatus().name() : null)
        .regDate(pet.getRegDate())
        .modDate(pet.getModDate())
        .mid(pet.getMember() != null ? pet.getMember().getMid() : null)
        .imagePath(pet.getImagePath())
        .thumbnailPath(pet.getThumbnailPath())
        .build();
  }
}