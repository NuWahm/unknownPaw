package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"member", "petOwner", "petSitter"})
@Table(name = "pet")
public class Pet extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long petId;

  private String petName; // 펫 이름
  private String breed; // 견종
  private int petBirth; // 펫 출생 연도
  private boolean petGender; // 성별
  private double weight; // 무게
  private String petMbti; // 강아지 성격
  private boolean neutering; // 중성화 여부
  private String petIntroduce; // 펫 소개

  @ManyToOne(fetch = FetchType.LAZY)
  private Member member; // 유저 정보 (펫 소유자 또는 시터)

  @ManyToOne(fetch = FetchType.LAZY)
  private Image imgId; // 이미지

  @ManyToOne(fetch = FetchType.LAZY)
  private PetOwner petOwner; // 펫 오너

  @ManyToOne(fetch = FetchType.LAZY)
  private PetSitter petSitter; // 펫 시터

  protected Pet(BaseEntityBuilder<?, ?> b) {
    super(b);
  }

  // 펫의 소유 여부에 따라 오너/시터 설정
  public void setOwnerOrSitter() {
    if (this.petOwner != null) {
      this.petSitter = null;  // 오너가 있으면 시터는 null
    } else if (this.petSitter != null) {
      this.petOwner = null;  // 시터가 있으면 오너는 null
    }
  }
}
