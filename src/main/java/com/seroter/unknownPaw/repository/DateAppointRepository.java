package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.DateAppoint;
import com.seroter.unknownPaw.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

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
}
