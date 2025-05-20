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
//    private final PasswordEncoder passwordEncoder;

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
                .build();
    }

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

    public MemberResponseDTO getSimpleProfileInfo(Long mid) {
        Object result = memberRepository.findSimpleProfileInfo(mid)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        if (!(result instanceof Object[] objects)) {
            throw new IllegalStateException("예상치 못한 쿼리 결과 형식");
        }

        Long memberId = ((Number) objects[0]).longValue();
        String nickname = (String) objects[1];
        Integer pawRate = objects[2] != null ? ((Number) objects[2]).intValue() : 0;
        String profileImagePath = (String) objects[3];

        return MemberResponseDTO.builder()
                .mid(memberId)
                .nickname(nickname)
                .pawRate(pawRate)
                .profileImagePath(profileImagePath)
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
        Object[] result = memberRepository.findMyActivityStats(mid);

        // 방어적 캐스팅: Number → Long
        Long memberId = ((Number) result[0]).longValue();
        Long petOwnerPostCount = ((Number) result[1]).longValue();
        Long petSitterPostCount = ((Number) result[2]).longValue();
        Long dateAppointCount = ((Number) result[3]).longValue();

        return new Object[]{memberId, petOwnerPostCount, petSitterPostCount, dateAppointCount};
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

    }

