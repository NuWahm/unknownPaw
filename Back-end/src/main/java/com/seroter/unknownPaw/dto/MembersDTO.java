package com.seroter.unknownPaw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MembersDTO {
  private Long mid;

  private String email;
  private String name;
  private String nickname;
  private String phonenumber;
  private int birthday;
  private Boolean gender;
  private String adress;
  private float pawRate;

}
