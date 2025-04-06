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
@Table(name = "pets")
public class Pet extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long petId;

  private String petName;
  private String breed;
  private int petBirth;
  private boolean petGender;
  private double weight;
  private String petMbti; // 강아지 성격
  private boolean neutering;
  private String petIntroduce;


  private LocalDateTime regDate;
  private LocalDateTime modDate;


  @ManyToOne
  private Members members;

  @ManyToOne(fetch = FetchType.LAZY)
  private Long imgId;

  @ManyToOne(fetch = FetchType.LAZY)
  private Long petOwnerId;

}