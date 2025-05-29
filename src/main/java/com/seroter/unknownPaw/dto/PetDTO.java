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
    this.status = pet.getStatus() != null ? pet.getStatus().name() : null;
    this.regDate = pet.getRegDate();
    this.modDate = pet.getModDate();
  }




  public Long getPetId() {
    return petId;
  }

  public void setPetId(Long petId) {
    this.petId = petId;
  }

  public String getPetName() {
    return petName;
  }

  public void setPetName(String petName) {
    this.petName = petName;
  }

  public String getBreed() {
    return breed;
  }

  public void setBreed(String breed) {
    this.breed = breed;
  }

  public int getPetBirth() {
    return petBirth;
  }

  public void setPetBirth(int petBirth) {
    this.petBirth = petBirth;
  }

  public boolean isPetGender() {
    return petGender;
  }

  public void setPetGender(boolean petGender) {
    this.petGender = petGender;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public String getPetMbti() {
    return petMbti;
  }

  public void setPetMbti(String petMbti) {
    this.petMbti = petMbti;
  }

  public boolean isNeutering() {
    return neutering;
  }

  public void setNeutering(boolean neutering) {
    this.neutering = neutering;
  }

  public String getPetIntroduce() {
    return petIntroduce;
  }

  public void setPetIntroduce(String petIntroduce) {
    this.petIntroduce = petIntroduce;
  }

  public LocalDateTime getRegDate() {
    return regDate;
  }

  public void setRegDate(LocalDateTime regDate) {
    this.regDate = regDate;
  }

  public LocalDateTime getModDate() {
    return modDate;
  }

  public void setModDate(LocalDateTime modDate) {
    this.modDate = modDate;
  }
}
