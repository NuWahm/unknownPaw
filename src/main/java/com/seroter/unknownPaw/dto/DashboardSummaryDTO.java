package com.seroter.unknownPaw.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryDTO {
  private PostCount postCounts;
  private int likedCount;
  private ReservationCount reservations;
  private String latestPostTitle;
  private LocalDate latestReservationDate;
  private LocalDateTime latestPostDate;
  private LocalDateTime latestReservationDateTime;


  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PostCount {
    private int petOwner;
    private int petSitter;
    private int community;
  }

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ReservationCount {
    private int walk;
    private int care;
    private int hotel;
  }
}
