package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.ServiceCategory;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetOwnerRepository;
import com.seroter.unknownPaw.repository.search.SearchPetOwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetOwnerService {

    private final MemberRepository memberRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final SearchPetOwnerRepository searchpetOwnerRepository;

    // 등록
    public Long register(PostDTO dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        PetOwner entity = dtoToEntity(dto);
        entity.setMember(member);
        petOwnerRepository.save(entity);

        return entity.getPostId();
    }

    // 조회
    public PostDTO get(Long postId) {
        return petOwnerRepository.findById(postId)
                .map(this::entityToDTO)
                .orElseThrow(() -> new EntityNotFoundException("PetOwner not found: " + postId));
    }

    // 수정
    public void modify(PostDTO dto) {
        PetOwner entity = petOwnerRepository.findById(dto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("PetOwner not found: " + dto.getPostId()));

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setServiceCategory(ServiceCategory.valueOf(dto.getServiceCategory()));
        entity.setDesiredHourlyRate(dto.getHourlyRate());
        entity.setDefaultLocation(dto.getDefaultLocation());
        entity.setFlexibleLocation(dto.getFlexibleLocation());

        petOwnerRepository.save(entity);
    }

    // 삭제
    public void remove(Long postId) {
        petOwnerRepository.deleteById(postId);
    }

    // 검색
    public PageResultDTO<PostDTO, PetOwner> search(String keyword, String location, String category, Pageable pageable) {
        var result = searchpetOwnerRepository.searchDynamic(keyword, location, category, pageable);
        return new PageResultDTO<>(result, this::entityToDTO);
    }

    // DTO → Entity 변환
    public PetOwner dtoToEntity(PostDTO dto) {
        return PetOwner.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .serviceCategory(ServiceCategory.valueOf(dto.getServiceCategory()))
                .desiredHourlyRate(dto.getHourlyRate())
                .defaultLocation(dto.getDefaultLocation())
                .flexibleLocation(dto.getFlexibleLocation())
                .likes(dto.getLikes())
                .chatCount(dto.getChatCount())
                .build();
    }

    // Entity → DTO 변환
    public PostDTO entityToDTO(PetOwner entity) {
        return PostDTO.builder()
                .postId(entity.getPostId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .serviceCategory(entity.getServiceCategory().name())
                .hourlyRate(entity.getDesiredHourlyRate())
                .defaultLocation(entity.getDefaultLocation())
                .flexibleLocation(entity.getFlexibleLocation())
                .likes(entity.getLikes())
                .chatCount(entity.getChatCount())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .email(entity.getMember() != null ? entity.getMember().getEmail() : null)
                .isPetSitterPost(false)
                .build();
    }

}
