package com.seroter.unknownPaw.dto;

import lombok.*;

import java.time.LocalDateTime;

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

  private String defaultLocation; // ⭐ 위치 정보 (이전 답변에서 추가 요청)
  private String flexibleLocation; // 유동적인 위치

  // ⭐⭐⭐ 이 두 필드를 추가해야 합니다! ⭐⭐⭐
  private LocalDateTime confirmationDate; // 서비스 시작 날짜 (Backend LocalDateTime 타입)
  private LocalDateTime futureDate;       // 서비스 종료 날짜 (Backend LocalDateTime 타입)



}
