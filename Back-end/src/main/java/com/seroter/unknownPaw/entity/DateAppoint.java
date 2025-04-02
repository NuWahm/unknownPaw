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
@ToString(exclude = {"petOwner", "petSitter"})
public class DateAppoint extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long rno;


  private int chat;
  private int decideHourRate;
  private boolean readTheOriginalText;
  private boolean reservationStatus;

  @Enumerated(EnumType.STRING)
  private ServiceCategory serviceCategory;

  @Column(nullable = false)
  private LocalDateTime regDate;

  @Column(nullable = false) gi
  private LocalDateTime futureDate;

  // 남은 시간(분)을 계산하는 메서드
  public long getRemainingMinutes() {
    if (futureDate != null && regDate != null) {
      Duration duration = Duration.between(regDate, futureDate);
      return duration.toMinutes(); // 남은 시간을 분 단위로 반환
    }
    return 0;
  }


  @ManyToOne
  private Long pid;

  @ManyToOne
  private Long imgNo;

  @ManyToOne
  private Long mno;

  @ManyToOne
  private Long petOwnerId;

  @ManyToOne
  private Long petSitterId;



}
