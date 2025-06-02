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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.seroter.unknownPaw.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PetSitterRepository petSitterRepository;
    private final PasswordEncoder passwordEncoder;
    private final PetService petService;

    // [1] 회원가입
    public MemberResponseDTO register(MemberRequestDTO dto) {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new CustomException(EMAIL_ALREADY_EXISTS);
        }
        if (memberRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
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

        // Pet 등록
        if (dto.getPetInfo() != null) {
            petService.registerMyPet(member, dto.getPetInfo());
        }

        return MemberResponseDTO.builder()
            .mid(member.getMid())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .profileImagePath(member.getProfileImagePath())
            .pawRate(member.getPawRate())
            .build();
    }

    // [2] 로그인 인증
    public Optional<Member> authenticate(LoginRequestDTO dto) {
        return memberRepository.findByEmail(dto.getEmail())
            .filter(member -> passwordEncoder.matches(dto.getPassword(), member.getPassword()));
    }

    // [3] 회원 정보 단건 조회
    public MemberResponseDTO getMemberById(Long mid) {
        Member member = memberRepository.findById(mid)
            .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다. mid = " + mid));
        return new MemberResponseDTO(member);
    }

    // [4] 회원 정보 업데이트
    @Transactional
    public Member updateMember(Member member, MemberUpdateRequestDTO updateRequestDTO) {
        if (updateRequestDTO.getNickname() != null && !updateRequestDTO.getNickname().trim().isEmpty()) {
            String newNickname = updateRequestDTO.getNickname().trim();
            Optional<Member> existing = memberRepository.findByNickname(newNickname);
            if (existing.isPresent() && !existing.get().getMid().equals(member.getMid())) {
                throw new CustomException(NICKNAME_ALREADY_EXISTS, "이미 사용 중인 닉네임입니다.");
            }
            member.setNickname(newNickname);
        }
        if (updateRequestDTO.getAddress() != null) {
            member.setAddress(updateRequestDTO.getAddress());
        }
        if (updateRequestDTO.getPhoneNumber() != null) {
            member.setPhoneNumber(updateRequestDTO.getPhoneNumber());
        }
        return memberRepository.save(member);
    }

    // [5] 비밀번호 변경
    @Transactional
    public void changePassword(Member member, PasswordChangeRequestDTO dto) {
        if (!passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }
        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        member.setPassword(encodedNewPassword);
        memberRepository.save(member);
    }

    // [6] 특정 회원의 펫 리스트 반환 (PetService 위임)
    public List<PetDTO> getMemberPets(Long mid) {
        return petService.getPetsByMemberId(mid);
    }

    // [7] 특정 회원의 모든 글(Owner + Sitter) 리스트 반환
    public List<PostDTO> getMemberPosts(Long mid) {
        List<Post> allPosts = new ArrayList<>();
        allPosts.addAll(petOwnerRepository.findByMember_Mid(mid));
        allPosts.addAll(petSitterRepository.findByMember_Mid(mid));
        allPosts.sort(Comparator.comparing(Post::getRegDate).reversed());
        return allPosts.stream().map(PostDTO::fromEntity).collect(Collectors.toUnmodifiableList());
    }

    // [8] 회원 탈퇴 (마스킹)
    @Transactional
    public void withdrawMember(Long memberId, MemberRequestDTO requestDTO) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        if (!member.isFromSocial() && requestDTO != null && requestDTO.getPassword() != null) {
            if (!passwordEncoder.matches(requestDTO.getPassword(), member.getPassword())) {
                throw new CustomException(PASSWORD_REQUIRED);
            }
        } else if (!member.isFromSocial() && (requestDTO == null || requestDTO.getPassword() == null)) {
            throw new CustomException(PASSWORD_REQUIRED);
        }

        member.setStatus(Member.MemberStatus.DELETED);
        member.setModDate(LocalDateTime.now());
        member.setAddress(null);
        member.setPhoneNumber(null);
        member.setEmail("deleted_" + member.getMid() + "@example.com");
        memberRepository.save(member);
    }

    // [9] 중복 체크
    @Transactional(readOnly = true)
    public boolean isNicknameDuplicated(String nickname) {
        return nickname == null || nickname.trim().isEmpty() || memberRepository.findByNickname(nickname.trim()).isPresent();
    }
    public boolean checkEmailDuplication(String email) {
        return memberRepository.existsByEmail(email);
    }
    public boolean checkPhoneNumberDuplication(String phoneNumber) {
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }

    // [10] 간단 프로필(DTO) 반환
    public MemberResponseDTO.Simple getSimpleProfileInfo(Long mid) {
        return memberRepository.findSimpleProfileInfo(mid)
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    // [11] 나의 펫 포함 간단 프로필 (이메일 기준)
    public MemberResponseDTO getMySimpleProfileWithPets(String email) {
        Member member = memberRepository.findByEmailWithPets(email)
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
        return new MemberResponseDTO(member);
    }

    // [12] 이메일로 mid 찾기
    public Long getMemberIdByEmail(String email) {
        return memberRepository.findByEmail(email)
            .map(Member::getMid)
            .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));
    }

    // [13] 대시보드/통계/평점 관련
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

    // [14] 닉네임만 단독 업데이트
    public void updateNickname(Long memberId, String newNickname) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.setNickname(newNickname);
        memberRepository.save(member);
    }

    // [15] 상세 프로필 (펫 포함)
    public MemberResponseDTO getSimpleProfile(Long mid) {
        Member member = memberRepository.findSimpleProfile(mid)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));
        log.info("Found member: {}", member);
        log.info("Member's pets: {}", member.getPets());

        return MemberResponseDTO.builder()
            .mid(member.getMid())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .profileImagePath(member.getProfileImagePath())
            .pawRate(member.getPawRate())
            .gender(member.getGender())
            .introduce(member.getIntroduce())
            .emailVerified(member.isEmailVerified())
            .pets(member.getPets().stream()
                .map(PetDTO::fromEntity)
                .collect(Collectors.toList()))
            .build();
    }

    // 추가: 이메일 + 소셜로 회원 찾기
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    public Optional<Member> findByEmailAndFromSocial(String email, boolean fromSocial) {
        return memberRepository.findByEmailAndFromSocial(email, fromSocial);
    }

    // 추가: PetOwner/PetSitter 연관 데이터 조회
    public Member getMemberWithPetOwners(Long mid) {
        return memberRepository.findMemberWithPetOwners(mid)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    }
    public Member getMemberWithPetSitters(Long mid) {
        return memberRepository.findMemberWithPetSitters(mid)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    }

    // 추가: 전화번호 중복 체크 (개인정보 수정 시)
    public boolean isPhoneNumberExists(String phoneNumber) {
        return memberRepository.existsByPhoneNumber(phoneNumber);
    }
}