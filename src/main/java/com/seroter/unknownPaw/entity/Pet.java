package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"member", "petOwnerId", "petSitterId"})
public class Pet extends BaseEntity {  // BaseEntity 상속

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
  @JoinColumn(name = "member_id")
  private Member member; // 유저 정보 (펫 소유자 또는 시터)

  @ManyToOne(fetch = FetchType.LAZY)
  private Image imgId; // 이미지

  @Column(length = 300)
  private String imagePath;

  @Column(length = 300)
  private String thumbnailPath;

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public void updateImage(String path, String thumbPath) {
    this.imagePath     = path;
    this.thumbnailPath = thumbPath;
  }
  @ManyToOne(fetch = FetchType.LAZY)
  private PetOwner petOwnerId; // 펫 오너

  @ManyToOne(fetch = FetchType.LAZY)
  private PetSitter petSitterId; // 펫 시터

  public void setOwnerOrSitter() {
    if (this.petOwnerId != null) {
      this.petSitterId = null;  // 오너가 있으면 시터는 null
    } else if (this.petSitterId != null) {
      this.petOwnerId = null;  // 시터가 있으면 오너는 null
    }
  }

  public void setImgId(Image image) {
    this.imgId = image;
  }

  // pet 삭제 고려
  @Enumerated(EnumType.STRING) // Enum 타입을 DB에 String으로 저장
  @Column(nullable = false) // null을 허용하지 않음
  private PetStatus status;

  // 펫 상태를 정의하는 Enum
  public enum PetStatus {
    ACTIVE, DELETED // 활성, 삭제됨
  }
}