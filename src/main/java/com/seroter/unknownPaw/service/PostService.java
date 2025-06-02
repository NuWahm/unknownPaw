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
import java.util.*;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final PetOwnerRepository petOwnerRepository;
    private final PetSitterRepository petSitterRepository;
    private final SearchPostRepository searchPostRepository;

    // 게시글 등록 (문자열/Enum postType 모두 지원)
    public Long register(String postType, PostDTO dto, Long memberId) {
        PostType type = parsePostType(postType);
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Post entity = dtoToEntity(type, dto);
        entity.setMember(member);
        return savePostbyPostType(type, entity);
    }

    // 게시글 조회
    public PostDTO get(String postType, Long postId) {
        PostType type = parsePostType(postType);
        return findPostbyPostType(type, postId)
            .map(entity -> entityToDto(entity, type == PostType.PET_SITTER))
            .orElseThrow(() -> new EntityNotFoundException(type + " 게시글을 찾을 수 없습니다."));
    }

    // 게시글 수정
    public void modify(String postType, PostDTO dto) {
        PostType type = parsePostType(postType);
        Post entity = findPostbyPostType(type, dto.getPostId())
            .orElseThrow(() -> new EntityNotFoundException(type + " 게시글을 찾을 수 없습니다."));
        updateCommonFields(entity, dto);
        savePostbyPostType(type, entity);
    }

    // 게시글 삭제
    public void remove(String postType, Long postId) {
        PostType type = parsePostType(postType);
        switch (type) {
            case PET_OWNER -> petOwnerRepository.deleteById(postId);
            case PET_SITTER -> petSitterRepository.deleteById(postId);
        }
    }

    // 게시글 동적 검색
    @Transactional
    public Page<PostDTO> searchPosts(String postType, String keyword, String location, String category, Pageable pageable) {
        log.info("Searching posts with type: {}", postType);
        Page<? extends Post> result = searchPostRepository.searchDynamic(postType, keyword, location, category, pageable);
        log.info("Finished searching posts. Found {} elements.", result.getTotalElements());
        return result.map(post -> entityToDto(post, isSitter(postType)));
    }

    // 특정 멤버의 게시글 조회
    public List<PostDTO> getPostsByMember(PostType postType, Long memberId) {
        return switch (postType) {
            case PET_OWNER ->
                petOwnerRepository.findByMember_Mid(memberId).stream()
                    .map(post -> entityToDto(post, false)).toList();
            case PET_SITTER ->
                petSitterRepository.findByMember_Mid(memberId).stream()
                    .map(post -> entityToDto(post, true)).toList();
        };
    }

    // 특정 위치에 맞는 펫시터 게시글 조회
    public List<PostDTO> findSittersByLocation(String location) {
        return petSitterRepository.findByDefaultLocation(location)
            .stream()
            .map(post -> entityToDto(post, true))
            .toList();
    }

    // DTO → Entity 변환
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
        if (dto.getLicense() != null) builder.license(dto.getLicense());
        if (dto.getPetExperience() != null) builder.petExperience(dto.getPetExperience());
        return builder.build();
    }

    // 엔티티 → DTO 변환
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
            .isPetSitterPost(isSitter)
            .images(entity.getImages().stream()
                .map(img -> ImageDTO.builder()
                    .imgId(img.getImgId())
                    .path(img.getPath())
                    .thumbnailPath(img.getThumbnailPath())
                    .build())
                .collect(Collectors.toList()));

        if (entity.getMember() != null) {
            Member m = entity.getMember();
            builder.member(MemberResponseDTO.builder()
                .mid(m.getMid())
                .email(m.getEmail())
                .nickname(m.getNickname())
                .profileImagePath(m.getProfileImagePath())
                .pawRate(m.getPawRate())
                .build());
        }
        return builder.build();
    }

    // 게시글 공통 필드 업데이트
    private void updateCommonFields(Post entity, PostDTO dto) {
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setServiceCategory(ServiceCategory.valueOf(dto.getServiceCategory()));
        entity.setHourlyRate(dto.getHourlyRate());
        entity.setDefaultLocation(dto.getDefaultLocation());
        entity.setFlexibleLocation(dto.getFlexibleLocation());
    }

    // 역할에 맞는 게시글 조회
    private Optional<Post> findPostbyPostType(PostType type, Long postId) {
        return switch (type) {
            case PET_OWNER -> petOwnerRepository.findById(postId).map(post -> (Post) post);
            case PET_SITTER -> petSitterRepository.findById(postId).map(post -> (Post) post);
        };
    }

    // Owner, Sitter 게시글 저장
    private Long savePostbyPostType(PostType type, Post entity) {
        return switch (type) {
            case PET_OWNER -> petOwnerRepository.save((PetOwner) entity).getPostId();
            case PET_SITTER -> petSitterRepository.save((PetSitter) entity).getPostId();
        };
    }

    // ============= 좋아요 기능 =============

    @org.springframework.transaction.annotation.Transactional
    public void likePost(Long memberId, Long postId, PostType postType) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        switch (postType) {
            case PET_OWNER -> {
                PetOwner post = petOwnerRepository.findById(postId).orElseThrow();
                member.getLikedPetOwner().add(post);
                post.setLikes(post.getLikes() + 1);
            }
            case PET_SITTER -> {
                PetSitter post = petSitterRepository.findById(postId).orElseThrow();
                member.getLikedPetSitter().add(post);
                post.setLikes(post.getLikes() + 1);
            }
        }
        memberRepository.save(member);
    }

    @org.springframework.transaction.annotation.Transactional
    public void unlikePost(Long memberId, Long postId, PostType postType) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        switch (postType) {
            case PET_OWNER -> {
                PetOwner post = petOwnerRepository.findById(postId).orElseThrow();
                member.getLikedPetOwner().remove(post);
                post.setLikes(post.getLikes() - 1);
            }
            case PET_SITTER -> {
                PetSitter post = petSitterRepository.findById(postId).orElseThrow();
                member.getLikedPetSitter().remove(post);
                post.setLikes(post.getLikes() - 1);
            }
        }
        memberRepository.save(member);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Set<? extends Post> getLikedPosts(Long memberId, PostType postType) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        return switch (postType) {
            case PET_OWNER -> member.getLikedPetOwner();
            case PET_SITTER -> member.getLikedPetSitter();
        };
    }

    public Set<PostDTO> getLikedPostDTOs(Long memberId, PostType postType) {
        Set<? extends Post> likedPosts = getLikedPosts(memberId, postType);
        return likedPosts.stream()
            .map(post -> entityToDto(post, postType == PostType.PET_SITTER))
            .collect(Collectors.toSet());
    }

    // ============= 최근 게시글 랜덤 6개 =============

    public List<PostDTO> getRandom6PetOwnerPosts() {
        return petOwnerRepository.findRecent7DaysPosts(LocalDateTime.now().minusDays(7))
            .stream().limit(6)
            .map(post -> entityToDto(post, false)).toList();
    }

    public List<PostDTO> getRandom6PetSitterPosts() {
        return petSitterRepository.findRecent7DaysPosts(LocalDateTime.now().minusDays(7))
            .stream().limit(6)
            .map(post -> entityToDto(post, true)).toList();
    }

    // ============= 유틸 =============

    private boolean isSitter(String postType) {
        return PostType.PET_SITTER.name().equalsIgnoreCase(postType) || "petSitter".equalsIgnoreCase(postType);
    }

    private PostType parsePostType(String postType) {
        if (postType == null) throw new IllegalArgumentException("postType은 null일 수 없습니다.");
        if ("petOwner".equalsIgnoreCase(postType)) return PostType.PET_OWNER;
        if ("petSitter".equalsIgnoreCase(postType)) return PostType.PET_SITTER;
        try {
            return PostType.valueOf(postType.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("지원하지 않는 PostType: " + postType);
        }
    }

    @Transactional
    public PostDTO modifyPost(PostDTO postDTO, PostType postType) {
        Post post = findPostbyPostType(postType, postDTO.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // 기존 게시글 정보 업데이트
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setServiceCategory(ServiceCategory.valueOf(postDTO.getServiceCategory()));
        post.setHourlyRate(postDTO.getHourlyRate());
        post.setServiceDate(postDTO.getServiceDate());
        post.setDefaultLocation(postDTO.getDefaultLocation());
        post.setFlexibleLocation(postDTO.getFlexibleLocation());
        post.setLatitude(postDTO.getLatitude());
        post.setLongitude(postDTO.getLongitude());
        post.setModDate(LocalDateTime.now());

        // 이미지 처리
        if (postDTO.getImages() != null && !postDTO.getImages().isEmpty()) {
            // 기존 이미지 삭제
            post.getImages().clear();

            // 새 이미지 저장
            List<Image> newImages = postDTO.getImages().stream()
                .map(imgDTO -> Image.builder()
                    .path(imgDTO.getPath())
                    .thumbnailPath(imgDTO.getThumbnailPath())
                    .post(post)
                    .build())
                .collect(Collectors.toList());

            post.getImages().addAll(newImages);
        }

        return entityToDto(post, postType == PostType.PET_SITTER);
    }
}