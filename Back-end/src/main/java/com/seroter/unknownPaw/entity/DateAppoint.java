package com.seroter.unknownPaw.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DateAppoint extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long rno; // 예약번호


  private int decideHourRate; // 확정시급
  private boolean readTheOriginalText;  // 원글보기
  private boolean reservationStatus; // 예약상태
  private String chat; //채팅
  private String defaultLocation; // 기본 위치
  private String flexibleLocation; // 유동적인 위치


  @Column(nullable = false)
  private LocalDateTime confirmationDate; // 예약 확정 날짜

  @Column(nullable = false)
  private LocalDateTime futureDate; // 예약 실행 날짜


  // 예약실행 남은시간 표시 메서드
  public long getRemainingMinutes() {
    if (futureDate != null && confirmationDate != null) {
      Duration duration = Duration.between(confirmationDate, futureDate);
      return duration.toMinutes(); // 남은 시간을 분 단위로 반환
    }
    return 0;
  }

  @Enumerated(EnumType.STRING)
  private ServiceCategory serviceCategory; // 서비스 카테고리(산책, 돌봄, 호텔)


  @ManyToOne
  @JoinColumn(name = "memberId")
  private Member mid;

  @ManyToOne
  @JoinColumn(name = "pet_id")
  private Pet petId;

  @ManyToOne
  @JoinColumn(name = "img_id")
  private Image imgId;

  @ManyToOne
  @JoinColumn(name = "owner_post_id")
  private PetOwner petOwnerPost;

  @ManyToOne
  @JoinColumn(name = "sitter_post_id")
  private PetSitter petSitterPost;
}