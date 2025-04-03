package com.seroter.unknownPaw.entity;


import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "members")
public class Members extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long mid;

  private String email;
  private String name;
  private String nickname;
  private String mobile;
  private int birthday;
  private Boolean gender;
  private String address;
  private Float pawRate;

  private LocalDateTime regDate;
  private LocalDateTime modDate;

  @ManyToOne
  private Long petNo;
  @ManyToOne
  private Long imgNo;
  @ManyToOne
  private Long pno;
  @ManyToOne
  private Long cmo;


}