package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.DateAppoint;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import com.seroter.unknownPaw.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DateAppointRepository extends JpaRepository<DateAppoint, Long> {

  // 회원 기준 예약 전체 조회
  List<DateAppoint> findByMid(Member member);

  // 펫오너 게시글 기준 예약 내역 조회
  List<DateAppoint> findByPetOwnerPost_PostId(Long postId);

  // 펫시터 게시글 기준 예약 내역 조회
  List<DateAppoint> findByPetSitterPost_PostId(Long postId);

  // 예약 상태별 조회 (확정/미확정)
  List<DateAppoint> findByReservationStatus(boolean reservationStatus);

  // 특정 시점 이후의 예약만 조회 (예: 향후 예약만 보기)
  List<DateAppoint> findByFutureDateAfter(LocalDateTime now);

  // 특정 회원 + 예약 상태 기준 조회 (예: 내가 예약한 확정된 목록만)
  List<DateAppoint> findByMidAndReservationStatus(Member member, boolean status);

  @Query("SELECT COUNT(d) FROM DateAppoint d WHERE d.mid.mid = :mid AND d.serviceCategory = :category")
  int countByMemberIdAndCategory(@Param("mid") Long mid, @Param("category") ServiceCategory category);

  // 최근 예약 날짜 가져오기
  @Query("SELECT d FROM DateAppoint d WHERE d.mid.mid = :mid ORDER BY d.futureDate DESC")
  List<DateAppoint> findTopByMemberIdOrderByFutureDateDesc(@Param("mid") Long mid);

}
