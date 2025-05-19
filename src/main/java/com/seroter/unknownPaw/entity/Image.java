package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@ToString(exclude = {"member", "pet", "petOwner", "petSitter"})
@Table(name = "image")

public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imgId; // 고유 키

  private String profileImg; // 프로필 사진
  private String uuid; // UUID
  private String path; // 파일 경로

  @Column(nullable = false)
  public static final int TYPE_PROFILE = 1;
  public static final int TYPE_PET     = 2;
  public static final int TYPE_POST    = 3;
  private int imageType; // 1-멤버, 2-펫, 3-포스트

  // 회원정보 참조 (멤버)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mid")

  private Member member;


  // 반려동물 정보 참조
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pet_id")

  private Pet pet;


  // 펫오너 포스트 참조
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pet_owner_id")
  private PetOwner petOwner;

  // 펫시터 포스트 참조
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pet_sitter_id")
  private PetSitter petSitter;
}