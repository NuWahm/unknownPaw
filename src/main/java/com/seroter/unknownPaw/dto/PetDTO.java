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


  private LocalDateTime regDate;
  private LocalDateTime modDate;

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
