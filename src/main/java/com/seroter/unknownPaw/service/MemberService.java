// MemberService.java
package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.LoginRequestDTO;
import com.seroter.unknownPaw.dto.MemberRequestDTO;
import com.seroter.unknownPaw.dto.MemberResponseDTO;
import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.Pet;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PetRepository petRepository;

    // ✅ 회원가입
    public MemberResponseDTO register(MemberRequestDTO dto) {
        Member member = buildMemberFromDTO(dto);
        memberRepository.save(member);
        return buildMemberResponseDTO(member);
    }

    // ✅ 로그인 인증 처리
    public Optional<Member> authenticate(LoginRequestDTO dto) {
        Optional<Member> memberOpt = memberRepository.findByEmail(dto.getEmail());
        if (memberOpt.isEmpty()) {
            return Optional.empty();
        }

        Member member = memberOpt.get();
        if (passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            return Optional.of(member);
        }

        return Optional.empty();
    }

    // ✅ 회원 기본 정보 조회 (mid)
    public MemberResponseDTO getMember(Long mid) {
        Member member = findMemberById(mid);
        return buildMemberResponseDTO(member);
    }

    // ✅ 회원 요약 정보 조회

    public MemberResponseDTO getSimpleProfile(Long mid) {
        Member member = memberRepository.findSimpleProfile(mid)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다."));

        // 디버깅을 위한 로그
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
                                .map((Pet pet) -> new PetDTO(
                                        pet.getPetId(),
                                        pet.getPetName(),
                                        pet.getBreed(),
                                        pet.getPetBirth(),
                                        pet.isPetGender(),
                                        pet.getWeight(),
                                        pet.getPetMbti(),
                                        pet.isNeutering(),
                                        pet.getPetIntroduce(),
                                        pet.getMember().getMid()
                                ))
                                .toList()
                )
                .build();
    }


    // ✅ 이메일로 회원 조회
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    // ✅ 소셜 로그인 시 이메일 + 소셜 여부로 회원 조회
    public Optional<Member> findByEmailAndFromSocial(String email, boolean fromSocial) {
        return memberRepository.findByEmailAndFromSocial(email, fromSocial);
    }

    // ✅ 회원 닉네임 수정
    public void updateNickname(Long memberId, String newNickname) {
        Member member = findMemberById(memberId);
        member.setNickname(newNickname);
        memberRepository.save(member);
    }

    // ✅ 대시보드 데이터 조회
    public List<Object[]> getDashboardData(Long mid) {
        return memberRepository.findMemberWithAllData(mid);
    }

    // ✅ 회원 활동 통계 조회
    public Object[] getMyActivityStats(Long mid) {
        return memberRepository.findMyActivityStats(mid);
    }

    // ✅ 회원 평점 조회
    public Float getPawRate(Long mid) {
        return memberRepository.findPawRateByMemberId(mid);
    }

    // ✅ 전체 회원 평점 조회 (관리자용)
    public List<Object[]> getAllMemberPawRates() {
        return memberRepository.findAllMemberPawRates();
    }

    private Member buildMemberFromDTO(MemberRequestDTO dto) {
        return Member.builder()
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
    }

    private MemberResponseDTO buildMemberResponseDTO(Member member) {
        return MemberResponseDTO.builder()
                .mid(member.getMid())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImagePath(member.getProfileImagePath())
                .pawRate(member.getPawRate())
                .gender(member.getGender())
                .introduce(member.getIntroduce())
                .emailVerified(member.isEmailVerified())
                .build();
    }

    private MemberResponseDTO buildMemberResponseDTOFromSimpleProfile(Object[] result, List<PetDTO> pets) {
        try {
            // mid 처리 - 더 자세한 타입 체크와 로깅
            Long mid;
            Object midValue = result[0];
            log.info("Processing mid: value = {}, type = {}",
                    midValue,
                    midValue != null ? midValue.getClass().getName() : "null");

            if (midValue == null) {
                throw new IllegalArgumentException("mid cannot be null");
            }

            if (midValue instanceof Object[]) {
                // Object 배열인 경우 첫 번째 요소 사용
                Object[] arr = (Object[]) midValue;
                if (arr.length > 0 && arr[0] != null) {
                    log.info("Found array for mid, using first element: {}, type: {}",
                            arr[0], arr[0].getClass().getName());
                    midValue = arr[0];
                } else {
                    throw new IllegalArgumentException("Invalid mid array");
                }
            }

            // 다양한 숫자 타입 처리
            if (midValue instanceof Number) {
                mid = ((Number) midValue).longValue();
            } else if (midValue instanceof String) {
                mid = Long.parseLong((String) midValue);
            } else {
                log.error("Unexpected mid type: {}", midValue.getClass().getName());
                throw new IllegalArgumentException("Invalid mid type: " + midValue.getClass().getName());
            }

            // 나머지 필드 처리
            String nickname = result[1] != null ? String.valueOf(result[1]) : null;
            Float pawRate = result[2] != null ?
                    (result[2] instanceof Number ? ((Number) result[2]).floatValue() :
                            Float.parseFloat(String.valueOf(result[2]))) : 0.0f;
            String profileImagePath = result[3] != null ? String.valueOf(result[3]) : null;
            String email = result[4] != null ? String.valueOf(result[4]) : null;
            Boolean gender = result[5] != null ?
                    (result[5] instanceof Boolean ? (Boolean) result[5] :
                            result[5] instanceof Number ? ((Number) result[5]).intValue() == 1 :
                                    Boolean.parseBoolean(String.valueOf(result[5]))) : false;
            String introduce = result[6] != null ? String.valueOf(result[6]) : null;
            Boolean emailVerified = result[7] != null ?
                    (result[7] instanceof Boolean ? (Boolean) result[7] :
                            result[7] instanceof Number ? ((Number) result[7]).intValue() == 1 :
                                    Boolean.parseBoolean(String.valueOf(result[7]))) : false;

            return MemberResponseDTO.builder()
                    .mid(mid)
                    .nickname(nickname)
                    .pawRate(pawRate)
                    .profileImagePath(profileImagePath)
                    .email(email)
                    .gender(gender)
                    .introduce(introduce)
                    .emailVerified(emailVerified)
                    .pets(pets)
                    .build();
        } catch (Exception e) {
            log.error("Error building MemberResponseDTO: {}", e.getMessage(), e);
            throw new IllegalStateException("프로필 데이터 변환 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private List<PetDTO> convertPetsToDTO(List<Pet> pets) {
        return pets.stream()
                .map(pet -> new PetDTO(
                        pet.getPetId(),
                        pet.getPetName(),
                        pet.getBreed(),
                        pet.getPetBirth(),
                        pet.isPetGender(),
                        pet.getWeight(),
                        pet.getPetMbti(),
                        pet.isNeutering(),
                        pet.getPetIntroduce(),
                        pet.getMember().getMid()
                ))
                .collect(Collectors.toList());
    }

    private Member findMemberById(Long mid) {
        return memberRepository.findByMid(mid)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    }
    // 타입 변환을 위한 유틸리티 메서드들
    private Long convertToLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String convertToString(Object value) {
        return value != null ? value.toString() : null;
    }

    private Float convertToFloat(Object value) {
        if (value == null) return 0.0f;
        if (value instanceof Number) return ((Number) value).floatValue();
        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }
    private Boolean convertToBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() == 1;
        String strValue = value.toString().toLowerCase();
        return "true".equals(strValue) || "1".equals(strValue) || "yes".equals(strValue);
    }

    // ✅ JWT 인증된 사용자 전용 – pets 포함된 심플 프로필
    public MemberResponseDTO getMySimpleProfileWithPets(String email) {
        Member member = memberRepository.findByEmailWithPets(email)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
        return new MemberResponseDTO(member);
    }
}