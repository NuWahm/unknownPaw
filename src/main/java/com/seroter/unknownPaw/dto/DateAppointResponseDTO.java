package com.seroter.unknownPaw.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateAppointResponseDTO {
  private Long rno;
  private String type;
  private String date;
  private String owner;
  private String sitter;
  private String petName;
  private String duration;
  private String price;
  private String rating;
  private int decideHourRate;
  private Long mid;
  private Long petId;
  private Long petOwnerPostId;
  private Long petSitterPostId;


}