package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.*;
import com.seroter.unknownPaw.dto.EditProfile.MemberUpdateRequestDTO;
import com.seroter.unknownPaw.dto.EditProfile.PasswordChangeRequestDTO;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.exception.CustomException;
import com.seroter.unknownPaw.exception.ErrorCode;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetOwnerRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import com.seroter.unknownPaw.repository.PetSitterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.seroter.unknownPaw.exception.ErrorCode.INVALID_PASSWORD;
import static com.seroter.unknownPaw.exception.ErrorCode.NICKNAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberService {

  private final MemberRepository memberRepository;
  private final PetRepository petRepository;
  private final PetOwnerRepository petOwnerRepository;
  private final PetSitterRepository petSitterRepository;
  private final PasswordEncoder passwordEncoder;

  public MemberResponseDTO register(MemberRequestDTO dto) {
    // 예시: 이메일 중복 확인
    if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS); // 적절한 예외 처리 (409 Conflict 등)
    }
// 예시: 닉네임 중복 확인  (전화번호 중복 확인 (필요하다면))
    if (memberRepository.findByNickname(dto.getNickname()).isPresent()) {
      throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS); // 적절한 예외 처리
    }


    Member member = Member.builder()
        .mid(dto.getMid())
        .email(dto.getEmail())
        .password(passwordEncoder.encode(dto.getPassword()))
        .name(dto.getName())
        .nickname(dto.getNickname())
        .phoneNumber(dto.getPhoneNumber())
        .birthday(dto.getBirthday())
        .gender(dto.getGender())
        .address(dto.getAddress())
        .fromSocial(dto.isFromSocial())
        .pawRate(0.0f)
        .profileImagePath(null)
        .emailVerified(false)
        .signupChannel(dto.getSignupChannel())
        .role(Member.Role.USER)
        .status(Member.MemberStatus.ACTIVE)
        .build();
    memberRepository.save(member);

    if (dto.getPetInfo() != null) {
      PetDTO petDTO = dto.getPetInfo();
      Pet pet = Pet.builder()
          .member(member) // member entity와 관계 설정
          .petName(petDTO.getPetName())
          .breed(petDTO.getBreed())
          .petBirth(petDTO.getPetBirth())
          .petGender(petDTO.isPetGender())
          .weight(petDTO.getWeight())
          .petMbti(petDTO.getPetMbti())
          .neutering(petDTO.isNeutering())
          .petIntroduce(petDTO.getPetIntroduce())
          .member(member)
          .build();
      petRepository.save(pet);
    }

    return MemberResponseDTO.builder()
        .mid(member.getMid())
        .email(member.getEmail())
        .nickname(member.getNickname())
        .profileImagePath(member.getProfileImagePath())
        .pawRate(member.getPawRate())
        .build();
  }

  // get member
  public MemberResponseDTO getMember(Long mid) {
    Member member = memberRepository.findByMid(mid)
        .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    return MemberResponseDTO.builder()
        .mid(member.getMid())
        .email(member.getEmail())
        .nickname(member.getNickname())
        .profileImagePath(member.getProfileImagePath())
        .pawRate(member.getPawRate())
        .build();
  }

  // 2-1. 나의 개인정보
  public MemberResponseDTO getMemberById(Long mid) {
    Member member = memberRepository.findById(mid)
        .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다. mid = " + mid));

    return new MemberResponseDTO(member);
  }

  // 2-2. 회원 정보 업데이트
  @Transactional
  public Member updateMember(Member member, MemberUpdateRequestDTO updateRequestDTO) {
    // 닉네임 업데이트 로직
    if (updateRequestDTO.getNickname() != null && !updateRequestDTO.getNickname().trim().isEmpty()) {
      String newNickname = updateRequestDTO.getNickname().trim();
      Optional<Member> existingMemberWithNickname = memberRepository.findByNickname(newNickname);

      // 2. 조회된 회원이 존재하고, 그 회원이 현재 업데이트하려는 본인이 아닌 경우 중복으로 판단
      if (existingMemberWithNickname.isPresent() && !existingMemberWithNickname.get().getMid().equals(member.getMid())) {
        // 중복 닉네임 예외 발생
        throw new CustomException(NICKNAME_ALREADY_EXISTS, "이미 사용 중인 닉네임입니다."); // 커스텀 예외 사용 권장
      }
      // 중복이 아니거나 본인인 경우에만 닉네임 업데이트
      member.setNickname(newNickname);
    }
    if (updateRequestDTO.getAddress() != null) {
      member.setAddress(updateRequestDTO.getAddress());
    }
    if (updateRequestDTO.getPhoneNumber() != null) {
      member.setPhoneNumber(updateRequestDTO.getPhoneNumber());
    }
    // 이미지는 나중에
    Member updatedMember = memberRepository.save(member);

    return updatedMember;
  }

  // 2-3. 회원 비밀번호 수정
  @Transactional // 데이터 변경이 발생하므로 트랜잭션 설정
  public void changePassword(Member member, PasswordChangeRequestDTO passwordChangeRequestDTO) {
    String currentPassword = passwordChangeRequestDTO.getCurrentPassword();
    String newPassword = passwordChangeRequestDTO.getNewPassword();
    //  현재 비밀번호 확인
    if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
      throw new CustomException(INVALID_PASSWORD);
    }
    //  새로운 비밀번호 유효성 검사 (필요하다면 추가)
    // 예: 비밀번호 길이, 복잡성 규칙 등 검사
    // if (newPassword == null || newPassword.length() < 8) {
    //     throw new IllegalArgumentException("새 비밀번호는 8자 이상이어야 합니다.");
    // }
    //  새로운 비밀번호 암호화
    String encodedNewPassword = passwordEncoder.encode(newPassword);

    member.setPassword(encodedNewPassword);
  }


  // ✨ 배열 문제로 수정
  public MemberResponseDTO getSimpleProfileInfo(Long mid) {
    Object[] rawResult = memberRepository.findSimpleProfileInfo(mid)
        .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

    if (rawResult.length == 0 || rawResult[0] == null) {
      throw new IllegalArgumentException("회원 정보를 찾았으나 데이터 형식이 올바르지 않습니다.");
    }
    Object[] actualDataArray = (Object[]) rawResult[0];

    if (actualDataArray.length < 4) {
      throw new IllegalArgumentException("회원 정보 데이터 요소가 부족합니다.");
    }

    for (int i = 0; i < actualDataArray.length; i++) {
      Object element = actualDataArray[i];
      String elementType = (element != null) ? element.getClass().getName() : "null";
    }

    return MemberResponseDTO.builder()
        .mid((Long) actualDataArray[0])
        .nickname((String) actualDataArray[1])
        .pawRate((Float) actualDataArray[2])
        .profileImagePath((String) actualDataArray[3])
        .build();
  }

  public Optional<Member> findByEmail(String email) {
    return memberRepository.findByEmail(email);
  }

  public Optional<Member> findByEmailAndFromSocial(String email, boolean fromSocial) {
    return memberRepository.findByEmailAndFromSocial(email, fromSocial);
  }

  public Member getMemberWithPetOwners(Long mid) {
    return memberRepository.findMemberWithPetOwners(mid)
        .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
  }

  public Member getMemberWithPetSitters(Long mid) {
    return memberRepository.findMemberWithPetSitters(mid)
        .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
  }

  public List<Object[]> getDashboardData(Long mid) {
    return memberRepository.findMemberWithAllData(mid);
  }

  public Object[] getMyActivityStats(Long mid) {
    return memberRepository.findMyActivityStats(mid);
  }

  public Float getPawRate(Long mid) {
    return memberRepository.findPawRateByMemberId(mid);
  }

  public List<Object[]> getAllMemberPawRates() {
    return memberRepository.findAllMemberPawRates();
  }

  public void updateNickname(Long memberId, String newNickname) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    member.setNickname(newNickname);
    memberRepository.save(member);
  }

  // ✨ 상대방의 프로필을 보기 위해 추가
  public List<PetDTO> getMemberPets(Long mid) {
    List<Pet> pets = petRepository.findAllByMemberId(mid);
    List<PetDTO> petDTOs = pets.stream().map(pet -> PetDTO.builder()
        .petId(pet.getPetId())
        .petName(pet.getPetName())
        .breed(pet.getBreed())
        .petBirth(pet.getPetBirth())
        .petMbti(pet.getPetMbti())
        .weight(pet.getWeight())
        .petIntroduce(pet.getPetIntroduce())
        .build()
    ).collect(Collectors.toUnmodifiableList());
    return petDTOs; // pet이 없으면 빈 리스트
  }

  public List<PostDTO> getMemberPosts(Long mid) {
    List<PetOwner> ownerPosts = petOwnerRepository.findByMember_Mid(mid);
    List<PetSitter> sitterPosts = petSitterRepository.findByMember_Mid(mid);
    // PetOwner와 PetSitter 글 리스트를 하나의 Post 리스트로 합치기
    List<Post> allPosts = new ArrayList<>();
    allPosts.addAll(ownerPosts); // PetOwner 리스트 추가
    allPosts.addAll(sitterPosts); // PetSitter 리스트 추가

    allPosts.sort((p1, p2) -> p2.getRegDate().compareTo(p1.getRegDate()));

    List<PostDTO> postDTOs = allPosts.stream()
        .map(post -> PostDTO.fromEntity(post))
        .collect(Collectors.toUnmodifiableList());

    return postDTOs;
  }

  // ✨ 회원 탈퇴
  @Transactional
  public void withdrawMember(Long memberId, MemberRequestDTO requestDTO) {
    // 1. 회원 존재 여부 확인
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)); // 예외 처리 필요

    // 2. 현재 비밀번호 확인 (선택 사항: DTO를 사용하는 경우)
    // 소셜 로그인 회원이거나 비밀번호 확인 절차가 필요 없다면 이 부분은 건너뛸 수 있습니다.
    if (!member.isFromSocial() && requestDTO != null && requestDTO.getPassword() != null) {
      if (!passwordEncoder.matches(requestDTO.getPassword(), member.getPassword())) {
        throw new CustomException(ErrorCode.PASSWORD_REQUIRED); // 예외 처리 필요
      }
    } else if (!member.isFromSocial() && (requestDTO == null || requestDTO.getPassword() == null)) {
      // 일반 로그인 회원인데 비밀번호가 제공되지 않았을 경우 (필수 검증 시)
      throw new CustomException(ErrorCode.PASSWORD_REQUIRED); // 예외 처리 필요
    }

    // 3. 회원 상태를 DELETED로 변경
    member.setStatus(Member.MemberStatus.DELETED);
    // BaseEntity에 modDate가 있다면 업데이트
    member.setModDate(LocalDateTime.now()); // 최종 수정일 업데이트 (BaseEntity에 setter가 있다면)

    // 4. (선택 사항) 개인 정보 마스킹/삭제
    // GDPR 등 규정을 준수하려면 민감한 개인 정보를 이때 null 또는 마스킹 처리할 수 있습니다.
    // 예:
    member.setAddress(null);
    member.setPhoneNumber(null);
    member.setEmail("deleted_" + member.getMid() + "@example.com");

    memberRepository.save(member);
  }

  // * 이메일을 통해 회원의 고유 ID (mid)를 조회합니다.
  public Long getMemberIdByEmail(String email) {
    return memberRepository.findByEmail(email)
        .map(Member::getMid) // Member 객체에서 mid만 추출
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)); // 회원을 찾을 수 없을 때 예외 발생
  }

}
