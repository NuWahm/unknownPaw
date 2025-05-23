package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"member", "imgId"})
public class Pet extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long petId; // 펫 고유 ID

  private String petName; // 펫 이름
  private String breed; // 견종
  private int petBirth; // 펫 출생 연도
  private boolean petGender; // 성별 (true: 수컷, false: 암컷)
  private double weight; // 무게
  private String petMbti; // 강아지 성격
  private boolean neutering; // 중성화 여부
  private String petIntroduce; // 펫 소개

  // 연관된 회원 (소유자)
  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  // 이미지 정보
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "img_id")
  private Image imgId; // 이미지

  // 이미지 설정
  public void setImgId(Image image) {
    this.imgId = image;
  }

}
