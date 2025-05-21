package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.*;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.entity.Enum.ServiceCategory;
import com.seroter.unknownPaw.repository.MemberRepository;
import com.seroter.unknownPaw.repository.PetOwnerRepository;
import com.seroter.unknownPaw.repository.PetSitterRepository;
import com.seroter.unknownPaw.repository.search.SearchPostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostService {

  private final MemberRepository memberRepository; // 회원 정보 조회
  private final PetOwnerRepository petOwnerRepository; // 펫오너 게시글 조회 및 관리
  private final PetSitterRepository petSitterRepository; // 펫시터 게시글 조회 및 관리
  private final SearchPostRepository searchPostRepository; // 동적 게시글 검색 기능

  // 게시글 등록 메서드
  public Long register(PostType postType, PostDTO dto, Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    Post entity = dtoToEntity(dto, String.valueOf(postType));
    entity.setMember(member);

    return savePostbyPostType(String.valueOf(postType), entity);
  }

  // 게시글 조회 메서드
  public PostDTO get(String postType, Long postId) {
    return findPostbyPostType(postType, postId)
        .map(entity -> entityToDto(entity, isSitter(postType)))
        .orElseThrow(() -> new EntityNotFoundException(postType + " 게시글을 찾을 수 없습니다."));
  }

  // 게시글 수정 메서드
  public void modify(String postType, PostDTO dto) {
    Post entity = findPostbyPostType(postType, dto.getPostId())
        .orElseThrow(() -> new EntityNotFoundException(postType + " 게시글을 찾을 수 없습니다."));

    updateCommonFields(entity, dto);
    savePostbyPostType(postType, entity);
  }

  // 게시글 삭제 메서드
  public void remove(String postType, Long postId) {
    if ("petOwner".equals(postType)) {
      petOwnerRepository.deleteById(postId);
    } else if ("petSitter".equals(postType)) {
      petSitterRepository.deleteById(postId);
    } else {
      throw new IllegalArgumentException("잘못된 역할입니다.");
    }
  }

  // 게시글 동적 검색 메서드
  @Transactional
  public Page<? extends Post> searchPosts(String postType, String keyword, String location, String category, Pageable pageable) {
    log.info("Searching posts with type: {}", postType);
    Page<? extends Post> result = searchPostRepository.searchDynamic(postType, keyword, location, category, pageable);
    log.info("Finished searching posts. Found {} elements.", result.getTotalElements());
    return result;
  }

  // 특정 멤버의 게시글 조회 메서드
  public List<PostDTO> getPostsByMember(String postType, Long memberId) {
    if ("petOwner".equals(postType)) {
      return petOwnerRepository.findByMember_Mid(memberId)
          .stream()
          .map(post -> entityToDto(post, false))
          .toList();
    } else if ("petSitter".equals(postType)) {
      return petSitterRepository.findByMember_Mid(memberId)
          .stream()
          .map(post -> entityToDto(post, true))
          .toList();
    } else {
      throw new IllegalArgumentException("잘못된 역할입니다.");
    }
  }

  // 특정 위치에 맞는 펫시터 게시글 조회 메서드
  public List<PostDTO> findSittersByLocation(String location) {
    return petSitterRepository.findByDefaultLocation(location)
        .stream()
        .map(post -> entityToDto(post, true))
        .toList();
  }

  // DTO → 엔티티 변환
  private Post dtoToEntity(PostDTO dto, String postType) {
    return "petOwner".equals(postType) ? createPetOwnerEntity(dto) : createPetSitterEntity(dto);
  }

  // 펫오너 엔티티 생성
  private Post createPetOwnerEntity(PostDTO postDTO) {
    return PetOwner.builder()
        .title(postDTO.getTitle())
        .content(postDTO.getContent())
        .serviceCategory(ServiceCategory.valueOf(postDTO.getServiceCategory()))
        .hourlyRate(postDTO.getHourlyRate())
        .likes(postDTO.getLikes())
        .chatCount(postDTO.getChatCount())
        .defaultLocation(postDTO.getDefaultLocation())
        .flexibleLocation(postDTO.getFlexibleLocation())
        .latitude(postDTO.getLatitude())           // 위도 추가
        .longitude(postDTO.getLongitude())         // 경도 추가
        .build();
  }

  // 펫시터 엔티티 생성
  private Post createPetSitterEntity(PostDTO postDTO) {
    return PetSitter.builder()
        .title(postDTO.getTitle())
        .content(postDTO.getContent())
        .serviceCategory(ServiceCategory.valueOf(postDTO.getServiceCategory()))
        .hourlyRate(postDTO.getHourlyRate())
        .likes(postDTO.getLikes())
        .chatCount(postDTO.getChatCount())
        .defaultLocation(postDTO.getDefaultLocation())
        .flexibleLocation(postDTO.getFlexibleLocation())
        .latitude(postDTO.getLatitude())           // 위도 추가
        .longitude(postDTO.getLongitude())         // 경도 추가
        .build();
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
        .latitude(entity.getLatitude())            // 위도 매핑
        .longitude(entity.getLongitude())          // 경도 매핑
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
      log.debug("Mapped and set Member DTO for post ID: {}", entity.getPostId());
    } else {
      log.warn("Post entity with ID {} has a null member during entityToDto mapping.", entity.getPostId());
    }

    PostDTO builtDto = builder.build();
    log.debug("Finished building PostDTO for post ID: {}", builtDto.getPostId());
    return builtDto;
  }

  // 게시글 공통 필드 업데이트
  private void updateCommonFields(Post entity, PostDTO dto) {
    entity.setTitle(dto.getTitle());
    entity.setContent(dto.getContent());
    entity.setServiceCategory(ServiceCategory.valueOf(dto.getServiceCategory()));
    entity.setHourlyRate(dto.getHourlyRate());
    entity.setDefaultLocation(dto.getDefaultLocation());
    entity.setFlexibleLocation(dto.getFlexibleLocation());

    // 위도, 경도도 수정 반영
    entity.setLatitude(dto.getLatitude());
    entity.setLongitude(dto.getLongitude());
  }

  // 역할에 따른 게시글 조회
  private Optional<Post> findPostbyPostType(String postType, Long postId) {
    log.debug("Finding post by type {} and ID {}", postType, postId);
    if (PostType.PET_OWNER.name().equals(postType)) {
      return petOwnerRepository.findById(postId).map(post -> (Post) post);
    } else if (PostType.PET_SITTER.name().equals(postType)) {
      return petSitterRepository.findById(postId).map(post -> (Post) post);
    } else {
      throw new IllegalArgumentException("알 수 없는 게시글 타입 문자열입니다: " + postType);
    }
  }

  // 역할에 따른 게시글 저장
  private Long savePostbyPostType(String postType, Post entity) {
    if ("petOwner".equals(postType)) {
      return petOwnerRepository.save((PetOwner) entity).getPostId();
    } else if ("petSitter".equals(postType)) {
      return petSitterRepository.save((PetSitter) entity).getPostId();
    } else {
      throw new IllegalArgumentException("잘못된 역할입니다.");
    }
  }

  // 펫시터 여부 판단
  private boolean isSitter(String postType) {
    return PostType.PET_SITTER.name().equals(postType);
  }

  // 최근 7일 이내 펫오너 게시글 랜덤 6개
  public List<PostDTO> getRandom6PetOwnerPosts() {
    List<PetOwner> posts = petOwnerRepository.findRecent7DaysRandom6Posts();
    if (posts.isEmpty()) {
      // 최근 7일 이내 데이터가 없으면 전체 데이터에서 랜덤으로 가져오기
      posts = petOwnerRepository.findAll().stream()
          .limit(6)
          .collect(Collectors.toList());
    }
    return posts.stream()
        .map(post -> entityToDto(post, false))
        .toList();
  }

  public List<PostDTO> getRandom6PetSitterPosts() {
    List<PetSitter> posts = petSitterRepository.findRecent7DaysRandom6Posts();
    if (posts.isEmpty()) {
      // 최근 7일 이내 데이터가 없으면 전체 데이터에서 랜덤으로 가져오기
      posts = petSitterRepository.findAll().stream()
          .limit(6)
          .collect(Collectors.toList());
    }
    return posts.stream()
        .map(post -> entityToDto(post, true))
        .toList();
  }


  // 펫오너,펫시터 좋아요 등록
  @Transactional
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
      default -> throw new IllegalArgumentException("지원하지 않는 PostType입니다.");
    }

    memberRepository.save(member);
  }


  // 펫오너, 시터 좋아요 취소
  @Transactional
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
      default -> throw new IllegalArgumentException("지원하지 않는 PostType입니다.");
    }

    memberRepository.save(member);
  }

  // 펫 오너 시터 좋아요 한 글 목록 조회
  @Transactional(readOnly = true)
  public Set<? extends Post> getLikedPosts(Long memberId, PostType postType) {
    Member member = memberRepository.findById(memberId).orElseThrow();

    return switch (postType) {
      case PET_OWNER -> member.getLikedPetOwner();
      case PET_SITTER -> member.getLikedPetSitter();
      default -> throw new IllegalArgumentException("지원하지 않는 PostType입니다.");
    };
  }

  // entityToDto 가 private 선언이 되어있어 접근하기 위한 메서드 컨트롤러에서 좋아요 누른글 조회
  public Set<PostDTO> getLikedPostDTOs(Long memberId, PostType postType) {
    Set<? extends Post> likedPosts = getLikedPosts(memberId, postType);

    return likedPosts.stream()
        .map(post -> entityToDto(post, postType == PostType.PET_SITTER))
        .collect(Collectors.toSet());
  }
}






