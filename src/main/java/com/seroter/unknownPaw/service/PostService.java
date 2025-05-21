package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.*;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetOwnerRepository;
import com.seroter.unknownPaw.repository.PetRepository;
import com.seroter.unknownPaw.repository.PetSitterRepository;
import com.seroter.unknownPaw.repository.search.SearchPostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PetSitterRepository petSitterRepository;
    private final SearchPostRepository searchPostRepository;

    // 게시글 등록
    public Long register(String postType, PostDTO dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        PostType type = PostType.from(postType); // Enum 변환
        Post entity = dtoToEntity(type, dto);
        entity.setMember(member);
        return savePostbyPostType(type, entity);
    }

    // 게시글 조회
    public PostDTO get(String postType, Long postId) {
        PostType type = PostType.from(postType);
        return findPostbyPostType(type, postId)
                .map(entity -> entityToDto(entity, type == PostType.PET_SITTER))
                .orElseThrow(() -> new EntityNotFoundException(type + " 게시글을 찾을 수 없습니다."));
    }

    // 게시글 수정
    public void modify(String postType, PostDTO dto) {
        PostType type = PostType.from(postType);
        Post entity = findPostbyPostType(type, dto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(type + " 게시글을 찾을 수 없습니다."));
        updateCommonFields(entity, dto);
        savePostbyPostType(type, entity);
    }

    // 게시글 삭제
    public void remove(String postType, Long postId) {
        PostType type = PostType.from(postType);
        switch (type) {
            case PET_OWNER -> petOwnerRepository.deleteById(postId);
            case PET_SITTER -> petSitterRepository.deleteById(postId);
        }
    }

    // 게시글 동적 검색
    @Transactional
    public Page<? extends Post> searchPosts(String postType, String keyword, String location, String category, Pageable pageable) {
        log.info("Searching posts with type: {}", postType);
        return searchPostRepository.searchDynamic(postType, keyword, location, category, pageable);
    }

    // 특정 멤버의 게시글 조회
    public List<PostDTO> getPostsByMember(String postType, Long memberId) {
        PostType type = PostType.from(postType);
        return switch (type) {
            case PET_OWNER -> petOwnerRepository.findByMember_Mid(memberId)
                    .stream().map(post -> entityToDto(post, false)).toList();
            case PET_SITTER -> petSitterRepository.findByMember_Mid(memberId)
                    .stream().map(post -> entityToDto(post, true)).toList();
        };
    }

    // 특정 위치에 맞는 펫시터 게시글 조회
    public List<PostDTO> findSittersByLocation(String location) {
        return petSitterRepository.findByDefaultLocation(location)
                .stream()
                .map(post -> entityToDto(post, true))
                .toList();
    }

    // DTO를 엔티티로 변환하는 메서드
    private Post dtoToEntity(PostType type, PostDTO dto) {
        try {
            ServiceCategory.valueOf(dto.getServiceCategory());
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 서비스 카테고리");
        }
        return switch (type) {
            case PET_OWNER -> createPetOwnerEntity(dto);
            case PET_SITTER -> createPetSitterEntity(dto);
        };
    }

    // 펫오너 게시글 엔티티 생성
    private Post createPetOwnerEntity(PostDTO dto) {
        LocalDateTime parsedServiceDate = null;
        if (dto.getServiceDate() != null) {
            parsedServiceDate = dto.getServiceDate();
        }
        PetOwner.PetOwnerBuilder builder = PetOwner.builder()
                .postId(dto.getPostId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .serviceCategory(ServiceCategory.valueOf(dto.getServiceCategory()))
                .hourlyRate(dto.getHourlyRate())
                .serviceDate(dto.getServiceDate())
                .likes(dto.getLikes())
                .chatCount(dto.getChatCount())
                .defaultLocation(dto.getDefaultLocation())
                .flexibleLocation(dto.getFlexibleLocation())
                .member(null);
        if (dto.getPetId() != null) {
            Pet pet = petRepository.findById(dto.getPetId())
                    .orElseThrow(() -> new IllegalArgumentException("Pet not found"));
            builder.pet(pet);
        }
        return builder.build();
    }

    // 펫시터 게시글 엔티티 생성
    private Post createPetSitterEntity(PostDTO dto) {
        LocalDateTime parsedServiceDate = null;
        if (dto.getServiceDate() != null) {
            parsedServiceDate = dto.getServiceDate();
        }
        PetSitter.PetSitterBuilder builder = PetSitter.builder()
                .postId(dto.getPostId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .serviceCategory(ServiceCategory.valueOf(dto.getServiceCategory()))
                .hourlyRate(dto.getHourlyRate())
                .serviceDate(dto.getServiceDate())
                .likes(dto.getLikes())
                .chatCount(dto.getChatCount())
                .defaultLocation(dto.getDefaultLocation())
                .flexibleLocation(dto.getFlexibleLocation())
                .member(null);
        if (dto.getLicense() != null)
            builder.license(dto.getLicense());
        if (dto.getPetExperience() != null)
            builder.petExperience(dto.getPetExperience());

        return builder.build();
    }

    // 엔티티를 DTO로 변환
    private PostDTO entityToDto(Post entity, boolean isSitter) {
        PostDTO.PostDTOBuilder builder = PostDTO.builder()
                .postId(entity.getPostId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .serviceCategory(entity.getServiceCategory().name())
                .hourlyRate(entity.getHourlyRate())
                .likes(entity.getLikes())
                .chatCount(entity.getChatCount())
                .defaultLocation(entity.getDefaultLocation())
                .flexibleLocation(entity.getFlexibleLocation())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .isPetSitterPost(isSitter);

        if (entity.getMember() != null) {
            Member memberEntity = entity.getMember();
            log.debug("Mapping member for post ID {}", entity.getPostId());
            MemberResponseDTO memberDTO = MemberResponseDTO.builder()
                    .mid(memberEntity.getMid())
                    .email(memberEntity.getEmail())
                    .nickname(memberEntity.getNickname())
                    .profileImagePath(memberEntity.getProfileImagePath())
                    .pawRate(memberEntity.getPawRate())
                    .build();
            builder.member(memberDTO);
        } else {
            log.warn("Post entity with ID {} has a null member during entityToDto mapping.", entity.getPostId());
        }

        PostDTO builtDto = builder.build();
        log.debug("Finished building PostDTO for post ID: {}", builtDto.getPostId());
        return builtDto;
    }

    // 게시글의 공통 필드를 업데이트
    private void updateCommonFields(Post entity, PostDTO dto) {
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setServiceCategory(ServiceCategory.valueOf(dto.getServiceCategory()));
        entity.setHourlyRate(dto.getHourlyRate());
        entity.setDefaultLocation(dto.getDefaultLocation());
        entity.setFlexibleLocation(dto.getFlexibleLocation());
    }

    // 역할에 맞는 게시글을 조회
    private Optional<Post> findPostbyPostType(PostType type, Long postId) {
        return switch (type) {
            case PET_OWNER -> petOwnerRepository.findById(postId).map(post -> (Post) post);
            case PET_SITTER -> petSitterRepository.findById(postId).map(post -> (Post) post);
        };
    }

    // Owner, Sitter 게시글을 저장
    private Long savePostbyPostType(PostType type, Post entity) {
        return switch (type) {
            case PET_OWNER -> petOwnerRepository.save((PetOwner) entity).getPostId();
            case PET_SITTER -> petSitterRepository.save((PetSitter) entity).getPostId();
        };
    }

    // 펫시터 여부 확인
    private boolean isSitter(String postType) {
        PostType type = PostType.from(postType);
        return type == PostType.PET_SITTER;
    }

    // 최근 7일 이내 펫오너 게시글 랜덤 6개
    public List<PostDTO> getRandom6PetOwnerPosts() {
        return petOwnerRepository.findRecent7DaysPosts(LocalDateTime.now().minusDays(7))
                .stream()
                .limit(6)
                .map(post -> entityToDto(post, false))
                .toList();
    }

    // 최근 7일 이내 펫시터 게시글 랜덤 6개
    public List<PostDTO> getRandom6PetSitterPosts() {
        return petSitterRepository.findRecent7DaysPosts(LocalDateTime.now().minusDays(7))
                .stream()
                .limit(6)
                .map(post -> entityToDto(post, true))
                .toList();
    }
}