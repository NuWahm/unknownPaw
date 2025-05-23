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
  private String petName;
  private String duration;
  private String price;
  private String rating;
  private int decideHourRate;
  private Double latitude;            // 위도
  private Double longitude;           // 경도
}
