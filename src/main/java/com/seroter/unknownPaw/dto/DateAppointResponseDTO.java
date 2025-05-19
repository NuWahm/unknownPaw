package com.seroter.unknownPaw.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateAppointResponseDTO {
  private Long rno;
  private String type;       // 산책 / 돌봄 / 호텔
  private String date;       // 예약 날짜 (futureDate)
  private String owner;      // 예약자 닉네임
  private String petName;    // 반려동물 이름
  private String duration;   // 예: "1시간"
  private String price;      // 예: "15000원"
  private String rating;     // 예: "4.5" (현재는 더미 데이터)
}
