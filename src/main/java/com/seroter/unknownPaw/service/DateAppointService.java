package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.DateAppointRequestDTO;
import com.seroter.unknownPaw.dto.DateAppointResponseDTO;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; // 서비스 예약시작일 날짜로 조회하기 위해 추가


import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DateAppointService {

  private final DateAppointRepository dateAppointRepository;
  private final MemberRepository memberRepository;
  private final PetRepository petRepository;
  private final ImageRepository imageRepository;
  private final PetOwnerRepository petOwnerRepository;
  private final PetSitterRepository petSitterRepository;


  // DateAppointService.java

  public DateAppointResponseDTO findById(Long rno) {
    DateAppoint appoint = dateAppointRepository.findById(rno)
        .orElseThrow(() -> new IllegalArgumentException("해당 예약 내역이 존재하지 않습니다. rno: " + rno));

    return toDTO(appoint);
  }


  // 예약 등록
  @Transactional
  public DateAppointResponseDTO create(DateAppointRequestDTO dto) {
    Member member = memberRepository.findById(dto.getMid())
            .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 회원입니다."));

    DateAppoint.DateAppointBuilder builder = DateAppoint.builder()
            .decideHourRate(dto.getDecideHourRate())
            .readTheOriginalText(dto.isReadTheOriginalText())
            .reservationStatus(true)
            .chat(dto.getChat())
            .defaultLocation(dto.getDefaultLocation())
            .flexibleLocation(dto.getFlexibleLocation())
            .confirmationDate(dto.getConfirmationDate())
            .futureDate(dto.getFutureDate())
            .serviceCategory(dto.getServiceCategory())
            .mid(member);

    // 🎯 오너가 시터 글에 예약한 경우
    if (dto.getPetId() != null && dto.getPetSitterPostId() != null) {
      Pet pet = petRepository.findById(dto.getPetId())
              .orElseThrow(() -> new IllegalArgumentException("❌ 해당 petId에 대한 펫이 존재하지 않습니다."));

      PetSitter petSitter = petSitterRepository.findById(dto.getPetSitterPostId())
              .orElseThrow(() -> new IllegalArgumentException("❌ 해당 시터 게시글이 존재하지 않습니다."));

      builder.petId(pet)
              .petSitterPost(petSitter);
    }

    // 🎯 시터가 오너 글에 예약한 경우
    if (dto.getPetOwnerPostId() != null) {
      PetOwner petOwner = petOwnerRepository.findById(dto.getPetOwnerPostId())
              .orElseThrow(() -> new IllegalArgumentException("❌ 해당 오너 게시글이 존재하지 않습니다."));

      builder.petOwnerPost(petOwner);
    }

    if (dto.getImgId() != null) {
      imageRepository.findById(dto.getImgId())
              .ifPresent(builder::imgId);
    }

    DateAppoint saved = dateAppointRepository.save(builder.build());
    return toDTO(saved);
  }


  // 예약 수정
  @Transactional
  public DateAppointResponseDTO update(Long rno, DateAppointRequestDTO dto) {
    DateAppoint appoint = dateAppointRepository.findById(rno)
        .orElseThrow(() -> new IllegalArgumentException("❌ 해당 예약 내역이 존재하지 않습니다."));

    Member member = memberRepository.findById(dto.getMid())
        .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 회원입니다."));

    appoint.setDecideHourRate(dto.getDecideHourRate());
    appoint.setReadTheOriginalText(dto.isReadTheOriginalText());
    appoint.setReservationStatus(true); // 다시 활성화
    appoint.setChat(dto.getChat());
    appoint.setDefaultLocation(dto.getDefaultLocation());
    appoint.setFlexibleLocation(dto.getFlexibleLocation());
    appoint.setConfirmationDate(dto.getConfirmationDate());
    appoint.setFutureDate(dto.getFutureDate());
    appoint.setServiceCategory(dto.getServiceCategory());
    appoint.setMid(member);

    // 역할 구분 처리
    if (dto.getPetId() != null) {
      // 🐶 오너가 예약한 경우 → petId 존재
      Pet pet = petRepository.findById(dto.getPetId())
          .orElseThrow(() -> new IllegalArgumentException("❌ 해당 펫이 존재하지 않습니다."));

      PetSitter petSitter = petSitterRepository.findById(dto.getPetSitterPostId())
              .orElseThrow(() -> new IllegalArgumentException("❌ 해당 시터 게시글이 존재하지 않습니다."));

      appoint.setPetId(pet);
      appoint.setPetSitterPost(petSitter);
      appoint.setPetOwnerPost(null); // 오너가 예약한 경우, 시터글만 유지

    } else if (dto.getPetSitterPostId() != null) {
      PetOwner petOwner = petOwnerRepository.findById(dto.getPetOwnerPostId())
          .orElseThrow(() -> new IllegalArgumentException("❌ 해당 오너 게시글이 존재하지 않습니다."));

      appoint.setPetId(null); // 시터는 펫 정보 없음
      appoint.setPetOwnerPost(petOwner);
      appoint.setPetSitterPost(null); // 시터가 예약한 경우, 오너글만 유지
    }

    // 이미지 변경 처리
    if (dto.getImgId() != null) {
      imageRepository.findById(dto.getImgId())
          .ifPresent(appoint::setImgId);
    } else {
      appoint.setImgId(null); // 없을 경우 null 처리
    }
    System.out.println("🐶 petSitterPostId = " + dto.getPetSitterPostId());
    System.out.println("🐶 petOwnerPostId = " + dto.getPetOwnerPostId());
    System.out.println("🐶 petId = " + dto.getPetId());


    return toDTO(appoint);
  }



  @Transactional
  public void cancel(Long rno) {
    DateAppoint appoint = dateAppointRepository.findById(rno).orElseThrow();
    appoint.setReservationStatus(false);
    dateAppointRepository.save(appoint);
  }

  @Transactional
  public void deleteById(Long rno) {
    dateAppointRepository.deleteById(rno);
  }

  @Transactional
  public DateAppointResponseDTO getById(Long rno) {
    return dateAppointRepository.findById(rno)
        .map(this::toDTO)
        .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));
  }

  @Transactional
  public List<DateAppointResponseDTO> getAllByMemberId(Long memberId) {


    boolean exists = memberRepository.existsById(memberId);

    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("❌ 존재하지 않는 회원 ID: " + memberId));

    return dateAppointRepository.findByMid(member).stream()
        .map(this::toDTO)
        .toList();
  }


  private DateAppoint buildEntityFromDTO(DateAppointRequestDTO dto) {
    return DateAppoint.builder()
        .decideHourRate(dto.getDecideHourRate())
        .readTheOriginalText(dto.isReadTheOriginalText())
        .reservationStatus(true)
        .chat(dto.getChat())
        .defaultLocation(dto.getDefaultLocation())
        .flexibleLocation(dto.getFlexibleLocation())
        .confirmationDate(dto.getConfirmationDate())
        .futureDate(dto.getFutureDate())
        .serviceCategory(dto.getServiceCategory())
        .mid(memberRepository.findById(dto.getMid()).orElseThrow())
        .petId(petRepository.findById(dto.getPetId()).orElse(null))
        .imgId(dto.getImgId() != null ? imageRepository.findById(dto.getImgId()).orElse(null) : null)
        .petOwnerPost(dto.getPetOwnerPostId() != null ? petOwnerRepository.findById(dto.getPetOwnerPostId()).orElse(null) : null)
        .petSitterPost(dto.getPetSitterPostId() != null ? petSitterRepository.findById(dto.getPetSitterPostId()).orElse(null) : null)
        .build();
  }

  private DateAppointResponseDTO toDTO(DateAppoint appoint) {
    String type = appoint.getServiceCategory() != null
        ? switch (appoint.getServiceCategory()) {
      case WALK -> "산책";
      case CARE -> "돌봄";
      case HOTEL -> "호텔";
    }
        : "기타";

    String date = appoint.getFutureDate() != null
        ? appoint.getFutureDate().toLocalDate().toString()
        : "미정";

    String owner = appoint.getMid() != null
        ? appoint.getMid().getNickname()
        : "알 수 없음";

    String sitter = appoint.getPetSitterPost() != null
        ? appoint.getPetSitterPost().getMember().getNickname()
        : null;// 예약내역 시터 구분을 위해 추가

    String petName = appoint.getPetId() != null
        ? appoint.getPetId().getPetName()
        : "미지정";

    String duration;
    if (appoint.getConfirmationDate() != null && appoint.getFutureDate() != null) {
      Duration dur = Duration.between(appoint.getConfirmationDate(), appoint.getFutureDate());

      long totalMinutes = dur.toMinutes();
      long days = totalMinutes / (24 * 60);
      long hours = (totalMinutes % (24 * 60)) / 60;
      long minutes = totalMinutes % 60;

      StringBuilder sb = new StringBuilder();
      if (days > 0) sb.append(days).append("일 ");
      if (hours > 0) sb.append(hours).append("시간 ");
      if (minutes > 0 || sb.length() == 0) sb.append(minutes).append("분"); // 0분도 표시

      duration = sb.toString().trim();
    } else {
      duration = "미정";
    }

    String price = appoint.getDecideHourRate() > 0
        ? appoint.getDecideHourRate() + "원"
        : "0원";
    System.out.println("👉 duration 최종 결과: " + duration);
    return DateAppointResponseDTO.builder()
        .rno(appoint.getRno())
        .type(type)
        .date(date)
        .owner(owner)// 예약내역 오너 구분 추가 위해
        .sitter(sitter)// 예약내역 시터 구분을 위해 추가
        .petName(petName)
        .duration(duration)
        .price(price)
        .rating("4.5")
        .decideHourRate(appoint.getDecideHourRate())
        .mid(appoint.getMid() != null ? appoint.getMid().getMid() : null)
        .petId(appoint.getPetId() != null ? appoint.getPetId().getPetId() : null)
        .petOwnerPostId(appoint.getPetOwnerPost() != null ? appoint.getPetOwnerPost().getPostId() : null)
        .petSitterPostId(appoint.getPetSitterPost() != null ? appoint.getPetSitterPost().getPostId() : null)

        .build();

  }
  // "내가 맡긴 서비스" → 오너로서 예약한 내역
  @Transactional
  public List<DateAppointResponseDTO> getAppointsAsOwner(Long mid) {
    return dateAppointRepository.findByPetOwnerPost_Member_Mid(mid)
        .stream()
        .map(this::toDTO)
        .toList();
  }

  //"내가 맡긴 서비스" → 시터로서 예약한 내역
  @Transactional
  public List<DateAppointResponseDTO> getAppointsAsSitter(Long mid) {
    return dateAppointRepository.findByPetSitterPost_Member_Mid(mid)
        .stream()
        .map(this::toDTO)
        .toList();
  }
  // 📅 날짜 범위로 오너 예약 조회
  @Transactional
  public List<DateAppointResponseDTO> getOwnerAppointmentsByDateRange(Long mid, LocalDateTime startDate, LocalDateTime endDate) {
    return dateAppointRepository.findOwnerAppointmentsByConfirmationDate(mid, startDate, endDate)
        .stream()
        .map(this::toDTO)
        .toList();
  }

  // 📅 날짜 범위로 시터 예약 조회
  @Transactional
  public List<DateAppointResponseDTO> getSitterAppointmentsByDateRange(Long mid, LocalDateTime startDate, LocalDateTime endDate) {
    return dateAppointRepository.findSitterAppointmentsByConfirmationDate(mid, startDate, endDate)
        .stream()
        .map(this::toDTO)
        .toList();
  }



}

