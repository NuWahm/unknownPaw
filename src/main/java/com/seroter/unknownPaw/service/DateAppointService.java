package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.DateAppointRequestDTO;
import com.seroter.unknownPaw.dto.DateAppointResponseDTO;
import com.seroter.unknownPaw.entity.DateAppoint;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

  @Transactional
  public DateAppointResponseDTO create(DateAppointRequestDTO dto) {
    DateAppoint dateAppoint = buildEntityFromDTO(dto);
    return toDTO(dateAppointRepository.save(dateAppoint));
  }

  @Transactional
  public DateAppointResponseDTO update(Long rno, DateAppointRequestDTO dto) {
    DateAppoint appoint = dateAppointRepository.findById(rno).orElseThrow();

    appoint.setDecideHourRate(dto.getDecideHourRate());
    appoint.setReadTheOriginalText(dto.isReadTheOriginalText());
    appoint.setReservationStatus(true);
    appoint.setChat(dto.getChat());
    appoint.setDefaultLocation(dto.getDefaultLocation());
    appoint.setFlexibleLocation(dto.getFlexibleLocation());
    appoint.setConfirmationDate(dto.getConfirmationDate());
    appoint.setFutureDate(dto.getFutureDate());
    appoint.setServiceCategory(dto.getServiceCategory());
    appoint.setMid(memberRepository.findById(dto.getMid()).orElseThrow());
    appoint.setPetId(petRepository.findById(dto.getPetId()).orElse(null));
    appoint.setImgId(dto.getImgId() != null ? imageRepository.findById(dto.getImgId()).orElse(null) : null);
    appoint.setPetOwnerPost(dto.getPetOwnerPostId() != null ? petOwnerRepository.findById(dto.getPetOwnerPostId()).orElse(null) : null);
    appoint.setPetSitterPost(dto.getPetSitterPostId() != null ? petSitterRepository.findById(dto.getPetSitterPostId()).orElse(null) : null);

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

    String petName = appoint.getPetId() != null
        ? appoint.getPetId().getPetName()
        : "미지정";

    String duration = appoint.getDecideHourRate() > 0
        ? appoint.getDecideHourRate() + "시간"
        : "미정";

    String price = appoint.getDecideHourRate() > 0
        ? (appoint.getDecideHourRate() * 10000) + "원"
        : "0원";

    return DateAppointResponseDTO.builder()
        .rno(appoint.getRno())
        .type(type)
        .date(date)
        .owner(owner)
        .petName(petName)
        .duration(duration)
        .price(price)
        .rating("4.5") // 추후 평균 평점 연동 예정
        .build();
  }

}
