package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.mapping.UniqueKey;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "pet")
public class Pet extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long petId; // 펫 고유 번호(PK)

  private String petName; // 펫 이름
  private String breed; // 견종
  private int petBirth; // 펫 출생 연도(예: 2025/01)
  private boolean petGender; // 성별 true = 수컷, false = 암컷
  private double weight; // 무게
  private String petMbti; // 강아지 성격
  private boolean neutering; // 중성화 여부
  private String petIntroduce; // 펫 소개


  private LocalDateTime regDate;
  private LocalDateTime modDate;


  @ManyToOne
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  private Long imgId;

  @ManyToOne(fetch = FetchType.LAZY)
  private Long petOwnerId;

}