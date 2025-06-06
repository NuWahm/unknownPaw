package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.DashboardSummaryDTO;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import com.seroter.unknownPaw.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final PetOwnerRepository petOwnerRepo;
  private final PetSitterRepository petSitterRepo;
  private final CommunityRepository communityRepo;
  private final DateAppointRepository dateAppointRepo;
  private final MemberRepository memberRepo;

  public DashboardSummaryDTO getDashboardSummary(Long mid) {
    // 1. 게시글 개수
    int petOwnerCount = petOwnerRepo.countByMember_Mid(mid);
    int petSitterCount = petSitterRepo.countByMember_Mid(mid);
    int communityCount = communityRepo.countByMember_Mid(mid);

    // 2. 좋아요 수 계산
    int likeCount = memberRepo.fetchWithLikes(mid)
        .map(member -> member.getLikedPetOwner().size()
            + member.getLikedPetSitter().size()
            + member.getLikedCommunity().size())
        .orElse(0);

    // 3. 예약 개수
    int walkCount = dateAppointRepo.countByMemberIdAndCategory(mid, ServiceCategory.WALK);
    int careCount = dateAppointRepo.countByMemberIdAndCategory(mid, ServiceCategory.CARE);
    int hotelCount = dateAppointRepo.countByMemberIdAndCategory(mid, ServiceCategory.HOTEL);

    // 4. 최근 게시글 정보(제목 + 날짜)
    LatestPostInfo postInfo = getLatestPostInfo(mid);

    // 5. 가장 최근 예약 날짜
    LocalDate latestReservationDate = dateAppointRepo.findTopByMemberIdOrderByFutureDateDesc(mid)
        .stream()
        .findFirst()
        .map(DateAppoint::getFutureDate)
        .map(LocalDateTime::toLocalDate)
        .orElse(null);

    // 6. DTO 리턴
    return DashboardSummaryDTO.builder()
        .postCounts(DashboardSummaryDTO.PostCount.builder()
            .petOwner(petOwnerCount)
            .petSitter(petSitterCount)
            .community(communityCount)
            .build())
        .likedCount(likeCount)
        .reservations(DashboardSummaryDTO.ReservationCount.builder()
            .walk(walkCount)
            .care(careCount)
            .hotel(hotelCount)
            .build())
        .latestPostTitle(postInfo.title)             // 제목
        .latestPostDate(postInfo.date)               // 작성 시간
        .latestReservationDate(latestReservationDate)
        .build();
  }

  // 🔽 최근 게시글 제목 + 작성 시간 반환하는 메서드
  private LatestPostInfo getLatestPostInfo(Long mid) {
    Optional<PetOwner> ownerOpt = petOwnerRepo.findTopByMember_MidOrderByRegDateDesc(mid);
    Optional<PetSitter> sitterOpt = petSitterRepo.findTopByMember_MidOrderByRegDateDesc(mid);
    Optional<Community> commuOpt = communityRepo.findTopByMember_MidOrderByRegDateDesc(mid);

    LocalDateTime latestDate = null;
    String latestTitle = "최근 게시글 없음";

    if (ownerOpt.isPresent()) {
      latestDate = ownerOpt.get().getRegDate();
      latestTitle = ownerOpt.get().getTitle();
    }
    if (sitterOpt.isPresent() && (latestDate == null || sitterOpt.get().getRegDate().isAfter(latestDate))) {
      latestDate = sitterOpt.get().getRegDate();
      latestTitle = sitterOpt.get().getTitle();
    }
    if (commuOpt.isPresent() && (latestDate == null || commuOpt.get().getRegDate().isAfter(latestDate))) {
      latestDate = commuOpt.get().getRegDate();
      latestTitle = commuOpt.get().getTitle();
    }

    return new LatestPostInfo(latestTitle, latestDate);
  }

  // 🔽 제목 + 날짜를 담기 위한 내부 클래스
  private static class LatestPostInfo {
    String title;
    LocalDateTime date;

    public LatestPostInfo(String title, LocalDateTime date) {
      this.title = title;
      this.date = date;
    }
  }
}
