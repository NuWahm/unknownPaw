package com.seroter.unknownPaw.controller;

import com.seroter.unknownPaw.dto.DateAppointRequestDTO;
import com.seroter.unknownPaw.dto.DateAppointResponseDTO;
import com.seroter.unknownPaw.service.DateAppointService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
@RequiredArgsConstructor
public class DateAppointController {

  private final DateAppointService dateAppointService;


  // 예약내역 불러오기
  @GetMapping("/member/{mid}")
  public List<DateAppointResponseDTO> getByMember(@PathVariable Long mid) {
    return dateAppointService.getAllByMemberId(mid);
  }


  // 예약내역 상세보기
  @GetMapping("/appointment/{rno}")
  public DateAppointResponseDTO getReservationDetail(@PathVariable Long rno) {
    return dateAppointService.findById(rno);
  }


  // [READ] 예약 번호로 단건 조회 (프론트에서 단건 상세보기 용도)
  @GetMapping("/{rno}")
  public DateAppointResponseDTO getOne(@PathVariable Long rno) {
    return dateAppointService.getById(rno);
  }

  // [CREATE] 예약 등록
  @PostMapping
  public DateAppointResponseDTO create(@RequestBody DateAppointRequestDTO dto) {
    return dateAppointService.create(dto);
  }

  // [UPDATE] 예약 수정
  @PutMapping("/{rno}")
  public DateAppointResponseDTO update(@PathVariable Long rno, @RequestBody DateAppointRequestDTO dto) {
    return dateAppointService.update(rno, dto);
  }

  // [CANCEL] 예약 취소 (예약 상태만 false 처리)
  @PutMapping("/{rno}/cancel")
  public void cancel(@PathVariable Long rno) {
    dateAppointService.cancel(rno);
  }


  // [DELETE] 예약 완전 삭제 (필요할 경우)
  @DeleteMapping("/{rno}")
  public void celete(@PathVariable Long rno) {
    dateAppointService.deleteById(rno);
  }
}
