package com.seroter.unknownPaw.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

// 충돌 해결본

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

@Table(name = "m_members")
public class Members extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)

  private long id;
  private String email;
  private String name;
  private String nickname;
  private String phonenumber;
  private int birthday;
  private Boolean gender;
  private String adress;
  private float pawindex;

//  @ElementCollection(fetch = FetchType.LAZY)
//  @Builder.Default
//  private Set<MembersRole> roleSet = new HashSet<>();
//
//  public void addMemberRole(MembersRole membersRole) {
//    roleSet.add(membersRole);
//  }
//}
//
//

@Table(name = "un_members")
public class Members {
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
