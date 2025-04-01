package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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
}
