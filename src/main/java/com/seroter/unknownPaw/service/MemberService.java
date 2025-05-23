package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.MemberRequestDTO;
import com.seroter.unknownPaw.dto.MemberResponseDTO;
import com.seroter.unknownPaw.dto.PetDTO;
import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetOwnerRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import com.seroter.unknownPaw.repository.PetSitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PetSitterRepository petSitterRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponseDTO register(MemberRequestDTO dto) {
        Member member = Member.builder()
                .mid(dto.getMid())
                .email(dto.getEmail())
//                .password(passwordEncoder.encode(dto.getPassword()))
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

    public MemberResponseDTO getSimpleProfileInfo(Long mid) {
        Object[] result = memberRepository.findSimpleProfileInfo(mid)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));
        return MemberResponseDTO.builder()
                .mid((Long) result[0])
                .nickname((String) result[1])
                .pawRate((Float) result[2])
                .profileImagePath((String) result[3])
                .build();
    }

    private Member findMemberById(Long mid) {
        return memberRepository.findByMid(mid)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
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
}
