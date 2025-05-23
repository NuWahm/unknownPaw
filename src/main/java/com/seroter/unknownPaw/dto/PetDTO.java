package com.seroter.unknownPaw.dto;

import com.seroter.unknownPaw.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

  private Long mid; // 회원 ID (foreign key 역할)

  // Pet 엔티티를 기반으로 DTO 변환
  public PetDTO(Pet pet) {
    this.petId = pet.getPetId();
    this.petName = pet.getPetName();
    this.breed = pet.getBreed();
    this.petBirth = pet.getPetBirth();
    this.petGender = pet.isPetGender();
    this.weight = pet.getWeight();
    this.petMbti = pet.getPetMbti();
    this.neutering = pet.isNeutering();
    this.petIntroduce = pet.getPetIntroduce();
    this.mid = pet.getMember() != null ? pet.getMember().getMid() : null; // ← 이거 추가!!
  }

}
