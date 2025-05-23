package com.seroter.unknownPaw.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateAppointRequestDTO {
  private int decideHourRate;
  private boolean readTheOriginalText;
  private String chat;
  private String defaultLocation;
  private String flexibleLocation;


  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime confirmationDate;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime futureDate;

  private ServiceCategory serviceCategory;

  private Long mid;
  private Long petId;
  private Long imgId;
  private Long petOwnerPostId;
  private Long petSitterPostId;
  private Long postId; // 예약 대상 글 ID (오너글 or 시터글)

}
