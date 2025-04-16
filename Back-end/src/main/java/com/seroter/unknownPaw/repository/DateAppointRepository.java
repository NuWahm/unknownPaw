
package com.seroter.unknownPaw.repository;

import com.seroter.unknownPaw.entity.DateAppoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface DateAppointRepository extends JpaRepository<DateAppoint, Long> {

  // 펫시터 ID로 내역 조회 (내역조회)
  List<DateAppoint> findByPetSitterId(Long petSitterId);

  // 펫오너 ID로 내역 조회 (내역조회)
  List<DateAppoint> findByPetOwnerId(Long petOwnerId);

  // 예약 상태로 필터링 (true = 확정됨, false = 미확정)
  List<DateAppoint> findByReservationStatus(boolean reservationStatus);

  // 남은예약 날짜 조회
  List<DateAppoint> findByFutureDateAfter(LocalDateTime now);


}
