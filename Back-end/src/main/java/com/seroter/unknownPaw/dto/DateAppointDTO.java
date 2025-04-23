package com.seroter.unknownPaw.dto;


import com.seroter.unknownPaw.entity.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DateAppointDTO {

  private String rno; // 예약번호
  private int decideHourRate; // 확정시급
  private boolean readTheOriginalText; // 원글보기
  private boolean reservationStatus; // 예약상태
  private String chat; // 채팅
  private String defaultLocation; // 기본 위치
  private String flexibleLocation; // 유동적인 위치
  private LocalDateTime confirmationDate; // 예약 확정 날짜
  private LocalDateTime futureDate; // 예약 실행 날짜
  private ServiceCategory serviceCategory; // 돌봄, 산책 ,호텔


}

