package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.PostDTO;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PetSitter;
import com.seroter.unknownPaw.entity.ServiceCategory;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetSitterRepository;
import com.seroter.unknownPaw.repository.search.SearchPetSitterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetSitterService {

    private final MemberRepository memberRepository;
    private final PetSitterRepository petSitterRepository;
    private final SearchPetSitterRepository searchPetSitterRepository;

    // 등록
    public Long register(PostDTO dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        PetSitter entity = dtoToEntity(dto);
        entity.setMember(member);
        petSitterRepository.save(entity);

        return entity.getPostId();
    }
    // 조회
    public PostDTO get(Long postId) {
        return petSitterRepository.findById(postId)
                .map(this::entityToDTO)
                .orElseThrow(() -> new EntityNotFoundException("PetSitter not found: " + postId));
    }

    // 수정
    public void modify(PostDTO dto) {
        PetSitter entity = petSitterRepository.findById(dto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("PetSitter not found: " + dto.getPostId()));

        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setServiceCategory(ServiceCategory.valueOf(dto.getServiceCategory()));
        entity.setDesiredHourlyRate(dto.getHourlyRate());
        entity.setDefaultLocation(dto.getDefaultLocation());
        entity.setFlexibleLocation(dto.getFlexibleLocation());

        petSitterRepository.save(entity);
    }

    // 삭제
    public void remove(Long postId) {
        petSitterRepository.deleteById(postId);
    }

    // 검색
    public PageResultDTO<PostDTO, PetSitter> search(String keyword, String location, String category, Pageable pageable) {
        var result = searchPetSitterRepository.searchDynamic(keyword, location, category, pageable);
        return new PageResultDTO<>(result, this::entityToDTO);
    }

    // DTO → Entity 변환
    public PetSitter dtoToEntity(PostDTO dto) {
        return PetSitter.builder()
                .titgitle(dto.getTitle())
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
    public PostDTO entityToDTO(PetSitter entity) {
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
                .isPetSitterPost(true)
                .build();
    }
}
