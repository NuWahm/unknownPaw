package com.seroter.unknownPaw.service;

import com.seroter.unknownPaw.dto.CommentDTO;
import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.dto.CommunityResponseDTO;
import com.seroter.unknownPaw.entity.*;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import com.seroter.unknownPaw.entity.Enum.ImageType;
import com.seroter.unknownPaw.repository.CommentRepository;
import com.seroter.unknownPaw.repository.CommunityImageRepository;
import com.seroter.unknownPaw.repository.CommunityRepository;
import com.seroter.unknownPaw.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class CommunityService {

  private final CommunityRepository communityRepository;
  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;
  private final ImageService imageService;

  private final CommunityImageRepository communityImageRepository;

  // ========== [게시글 등록] ==========
  @Transactional
  public Long createCommunityPost(Long memberId, CommunityRequestDTO communityRequestDTO) {
    // 회원 ID로 회원 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

    // 게시글 카테고리 변환 (안전하게 변환)
    CommunityCategory communityCategory = CommunityCategory.fromString(String.valueOf(communityRequestDTO.getCommunityCategory()));

    // 게시글 엔티티 생성
    Community community = Community.builder()
        .title(communityRequestDTO.getTitle())
        .content(communityRequestDTO.getContent())
        .communityCategory(communityCategory) // 문자열 → Enum
        .likes(0)
        .member(member)  // 게시글 작성자 설정
        .regDate(null)  // @PrePersist로 자동 설정

        .build();

    // 게시글 저장
    Community savedCommunity = communityRepository.save(community);
    return savedCommunity.getCommunityId();
  }

  // ========== [게시글 단건 조회] ==========
  public CommunityResponseDTO getCommunityPost(Long communityId) {
    Community community = communityRepository.findByCommunityId(communityId);
    if (community == null) {
      throw new IllegalArgumentException("Post not found");
    }

    // CommunityResponseDTO 생성 시, fromEntity 사용
    return CommunityResponseDTO.fromEntity(community);
  }

  // ========== [게시글 전체 조회] ==========
  public List<CommunityResponseDTO> getAllCommunityPosts() {
    List<Community> communities = communityRepository.findAllByOrderByRegDateDesc();
    return communities.stream()

        .map(CommunityResponseDTO::fromEntity)  // fromEntity 메서드를 이용해 변환
        .collect(Collectors.toList());
  }

  // ========== [게시글 수정] ==========
  @Transactional
  public CommunityResponseDTO updateCommunityPost(
      Long postId,
      Long memberId,
      String title,
      String content,
      String type,
      List<String> existingImageNames,
      List<MultipartFile> newImages
  ) throws IOException {
    Community community = communityRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));

    if (!community.getMember().getMid().equals(memberId)) {
      throw new IllegalArgumentException("게시글을 수정할 권한이 없습니다.");
    }

    CommunityCategory communityCategory = CommunityCategory.valueOf(type.toUpperCase()); // Enum.valueOf() 사용 (혹은 CommunityCategory.fromString(type))
    community.modify(title, content, communityCategory);

    List<CommunityImage> currentImagesInDb = communityImageRepository.findByCommunity(community);
    List<String> imagesToRemove = new ArrayList<>();

    for (CommunityImage currentImage : currentImagesInDb) {
      if (!existingImageNames.contains(currentImage.getCommunityImageUrl())) {
        imagesToRemove.add(currentImage.getCommunityImageUrl());
        communityImageRepository.delete(currentImage);
        log.info("DB에서 이미지 정보 삭제: {}", currentImage.getCommunityImageUrl());
      }
    }

    // --- 이 부분이 핵심 수정 ---
    for (String fullImagePath : imagesToRemove) { // fullImagePath는 "community/uuid_filename.jpg" 형태
      // fullImagePath에서 "community" 부분과 "uuid_filename.jpg" 부분을 분리
      String[] pathParts = fullImagePath.split("/", 2); // 최대 2개로 분리
      if (pathParts.length == 2) {
        String imageType = pathParts[0]; // 예: "community"
        String fileName = pathParts[1];  // 예: "uuid_filename.jpg"
        imageService.deleteImage(imageType, fileName); // ImageService의 시그니처에 맞게 2개 인자 전달
        log.info("파일 시스템에서 이미지 삭제: type={}, fileName={}", imageType, fileName);
      } else {
        log.warn("이미지 경로 포맷이 예상과 다릅니다: {}", fullImagePath);
        // 에러 처리 또는 스킵
      }
    }

    // 3. 새 이미지 업로드 및 게시글에 연결
    if (newImages != null && !newImages.isEmpty()) {
      for (MultipartFile file : newImages) {
        // ImageService.saveImage 호출 (여기서 `imageType`과 `targetType`을 String으로 넘겨줍니다)
        String savedPath = null;
        try {
          // ImageService의 saveImage 시그니처: (MultipartFile file, String imageType, String targetType, Long targetId, Long petId)
          // 여기서 첫 번째 인자 `imageType`은 Image 엔티티의 imageType(정수 코드)를 결정하는 용도로 사용됩니다.
          // ImageService 내부에서 "community" 문자열을 받아서 int 4로 매핑할 것입니다.
          savedPath = imageService.saveImage(file, "community", "community", postId, null); // petId는 null로 전달
        } catch (Exception e) {
          log.error("새 이미지 저장 실패: {}", e.getMessage(), e);
          throw new IOException("새 이미지 업로드 중 오류 발생", e);
        }

        CommunityImage newImage = CommunityImage.builder()
            .communityImageUrl(savedPath)
            .communityIsThumbnail(false)
            .community(community)
            .build();
        communityImageRepository.save(newImage);
        log.info("새 이미지 DB에 저장: {}", savedPath);
      }
    }


    Community savedCommunity = communityRepository.save(community);
    return CommunityResponseDTO.fromEntity(savedCommunity);
  }


  // ========== [개별 이미지 삭제 메서드] ==========
  // CommunityWritePage의 _handleRemoveExistingImage에서 호출됨
  @Transactional
  public void removeCommunityImage(Long postId, String imageName) throws IOException { // imageName은 "community/uuid_filename.jpg"
    Community community = communityRepository.findById(postId)
        .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다: " + postId));

    Optional<CommunityImage> imageOptional = communityImageRepository.findByCommunityAndCommunityImageUrl(community, imageName);
    if (imageOptional.isPresent()) {
      CommunityImage communityImage = imageOptional.get();

      // --- 이 부분이 핵심 수정 ---
      String[] pathParts = imageName.split("/", 2);
      if (pathParts.length == 2) {
        String imageType = pathParts[0];
        String fileName = pathParts[1];
        imageService.deleteImage(imageType, fileName); // <-- 2개의 인자로 호출
        log.info("개별 이미지 파일 시스템에서 삭제: type={}, fileName={}", imageType, fileName);
      } else {
        log.warn("이미지 경로 포맷이 예상과 다릅니다 (개별 삭제 중): {}", imageName);
      }
      // --- 여기까지 핵심 수정 ---

      communityImageRepository.delete(communityImage); // DB에서 이미지 정보 삭제
      log.info("개별 이미지 DB에서 정보 삭제: postId={}, imageName={}", postId, imageName);

    } else {
      log.warn("삭제할 이미지를 DB에서 찾을 수 없음: postId={}, imageName={}", postId, imageName);
      throw new IllegalArgumentException("삭제할 이미지를 찾을 수 없습니다: " + imageName);
    }
  }


  // ========== [게시글 삭제] ==========
  @Transactional
  public void deleteCommunityPost(Long communityId) {
    Community community = communityRepository.findById(communityId)
        .orElseThrow(() -> new IllegalArgumentException("Post not found"));

    // 1. 해당 게시글에 연결된 모든 댓글 삭제 (추가)
    commentRepository.deleteByCommunity(community); // 또는 communityId로 삭제하는 메서드 필요

    // 2. 해당 게시글에 연결된 모든 좋아요 관계 삭제 (추가)
    // Community 엔티티에 ManyToMany로 likedMembers가 있다면, 해당 관계를 끊어줘야 함.
    // 예를 들어 Member의 likedCommunity 필드에서 해당 community를 제거하는 로직이 필요할 수 있습니다.
    // 혹은 Like 엔티티가 별도로 있다면 likeRepository.deleteByCommunity(community);
    // 지금은 ManyToMany로 보이니 Member 엔티티의 likedCommunity List에서 해당 Community 제거 로직이 필요합니다.
    // 또는 CommunityRepository에 deleteAllLikedMembersByCommunityId(communityId) 같은 쿼리 필요.
    // 간략하게는 member.getLikedCommunity().remove(community)를 모든 해당 member에 대해 수행해야 합니다.
    // (이 부분은 Community 엔티티와 Member 엔티티 간의 연관 관계 설정에 따라 다름)
    // 일단은 생략하고 이미지 먼저 처리.

    // 3. 해당 게시글에 연결된 이미지들 삭제 (현재 코드 유지, 단 ImageService의 deleteImage(imageType, fileName) 호출 확인)
    List<CommunityImage> imagesToDelete = communityImageRepository.findByCommunity(community);
    for (CommunityImage image : imagesToDelete) {
      try {
        String fullImageUrl = image.getCommunityImageUrl(); // "community/파일명.jpg"
        String[] pathParts = fullImageUrl.split("/", 2);
        if (pathParts.length == 2) {
          String imageType = pathParts[0]; // "community"
          String fileName = pathParts[1];  // "파일명.jpg"
          imageService.deleteImage(imageType, fileName); // ImageService의 deleteImage(String, String) 호출
          communityImageRepository.delete(image); // DB에서 이미지 레코드 삭제
        } else {
          log.warn("이미지 경로 포맷이 예상과 다릅니다 (게시글 삭제 중): {}", fullImageUrl);
        }
      } catch (Exception e) {
        log.error("게시글 삭제 중 이미지 파일 삭제 실패: {}", image.getCommunityImageUrl(), e);
      }
    }

    // 4. 마지막으로 게시글 삭제
    communityRepository.delete(community);
    log.info("게시글 삭제 완료: {}", communityId);
  }
  // `CommentRepository`에 필요한 메서드 추가 예시:
// List<Comment> findByCommunity(Community community);
// void deleteByCommunity(Community community);
// 또는 @Query("DELETE FROM Comment c WHERE c.community.communityId = :communityId")
// @Modifying
// void deleteByCommunityId(@Param("communityId") Long communityId);


  // ========== [댓글 작성] ==========
  @Transactional

  public Long createComment(Long communityId, Long memberId, String content) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

    Community community = communityRepository.findByCommunityId(communityId);

    if (community == null) {
      throw new IllegalArgumentException("Post not found");
    }


    // 댓글 엔티티 생성 (생성 시간 수동 설정)


    Comment comment = Comment.builder()
        .content(content)
        .member(member)
        .community(community)
        .createdAt(LocalDateTime.now())
        .build();

    commentRepository.save(comment);
    return comment.getCommentId();
  }

  // ========== [댓글 조회] ==========

  public List<CommentDTO> getCommentsByCommunityId(Long communityId) {
    List<Comment> comments = commentRepository.findByCommunity_CommunityId(communityId);
    return comments.stream()
        .map(CommentDTO::new)
        .collect(Collectors.toList());
  }

  // ========== [댓글 수정] ==========
  @Transactional
  public void updateComment(Long commentId, Long memberId, String newContent) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    if (!comment.getMember().getMid().equals(memberId)) {
      throw new IllegalArgumentException("You can only update your own comment");
    }


    comment.setContent(newContent);

  }

  // ========== [댓글 삭제] ==========
  @Transactional
  public void deleteComment(Long commentId, Long memberId) {
    Comment comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

    if (!comment.getMember().getMid().equals(memberId)) {
      throw new IllegalArgumentException("You can only delete your own comment");
    }


    commentRepository.delete(comment);
  }

  // 커뮤니티 게시글에 이미지를 추가하는 메서드
//    @Transactional
//    public void addImagesToCommunity(Long communityId, List<String> imageUrls) {
//        Community community = communityRepository.findByCommunityId(communityId);
//        if (community == null) {
//            throw new IllegalArgumentException("Community post not found");
//        }
//
//        // 이미지 추가
//        for (String imageUrl : imageUrls) {
//            CommunityImage image = CommunityImage.builder()
//                    .communityImageUrl(imageUrl)
//                    .communityIsThumbnail(false)  // 기본적으로 썸네일은 아니라고 가정
//                    .community(community)  // 해당 커뮤니티 게시글과 연결
//                    .build();
//            communityImageRepository.save(image);  // 이미지 저장
//        }
//    }

  // 0607
  // 커뮤니티 게시글에 이미지를 추가하는 메서드
  @Transactional
  public void addImagesToCommunity(Long communityId, List<String> filePaths) { // filePaths는 imageService.saveImage가 반환한 "community/파일명.jpg" 형태
    Community community = communityRepository.findByCommunityId(communityId);
    if (community == null) {
      throw new IllegalArgumentException("Community post not found");
    }

    for (String filePath : filePaths) {
      // 이 filePath는 이미 imageService.saveImage에서 "community/파일명.jpg" 형태로 반환된 것입니다.
      // 따라서 여기서 fileName만 추출하여 저장하는 것은 잘못되었습니다.
      // DB에는 "community/파일명.jpg" 형태로 저장해야 합니다.

      CommunityImage image = CommunityImage.builder()
          .communityImageUrl(filePath) // ⭐ filePath를 그대로 저장 (예: "community/파일명.jpg")
          .communityIsThumbnail(false)
          .community(community)
          .build();
      communityImageRepository.save(image);
      log.info("새 이미지 DB에 저장: {}", filePath); // 로그에도 filePath 그대로 찍도록 수정
    }
  }

  // 특정 커뮤니티 게시글에 속한 이미지들 조회
  public List<CommunityImage> getCommunityImages(Long communityId) {
    return communityImageRepository.findByCommunity_CommunityId(communityId);
  }

  // 커뮤니티 게시글의 썸네일 이미지 조회
  public CommunityImage getThumbnailImage(Long communityId) {
    return communityImageRepository.findByCommunity_CommunityId(communityId)
        .stream()
        .filter(CommunityImage::isCommunityIsThumbnail)  // 썸네일 이미지 필터링
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Thumbnail image not found"));
  }

  // ========== [댓글 ID로 댓글 조회] ==========
  public Comment getCommentById(Long commentId) {
    return commentRepository.findByCommentId(commentId);

  }

  // 타입별로 조회
  public List<CommunityResponseDTO> getPostsByType(String type) {
    CommunityCategory category = CommunityCategory.valueOf(type);
    List<Community> posts = communityRepository.findByCommunityCategory(category);
    return posts.stream()
        .map(CommunityResponseDTO::fromEntity)
        .collect(Collectors.toList());
  }

  // 커뮤니티 좋아요 추가
  public void likePost(Long postId, Long memberId) {
    Community community = communityRepository.findById(postId).orElseThrow();
    Member member = memberRepository.findById(memberId).orElseThrow();
    member.getLikedCommunity().add(community);
    memberRepository.save(member);
  }

  // 커뮤니티 좋아요 취소
  public void unlikePost(Long postId, Long memberId) {
    Community community = communityRepository.findById(postId).orElseThrow();
    Member member = memberRepository.findById(memberId).orElseThrow();
    member.getLikedCommunity().remove(community);
    memberRepository.save(member);
  }

  // 좋아요 누른 커뮤니티 게시글 조회
  public List<CommunityResponseDTO> getLikedCommunityPosts(Long memberId) {
    List<Community> likedPosts = communityRepository.findByLikedMemberId(memberId);
    return likedPosts.stream()
        .map(CommunityResponseDTO::fromEntity)
        .collect(Collectors.toList());
  }

  // 커뮤니티 좋아요 누른 사람 확인
  public boolean isLikedByMember(Long postId, Long memberId) {
    return communityRepository.existsByCommunityIdAndLikedMembers_Mid(postId, memberId);
  }

  // 커뮤니티 좋아요 누른 갯수
  public int getLikeCount(Long postId) {
    Optional<Community> optional = communityRepository.findById(postId);
    if (optional.isPresent()) {
      return optional.get().getLikedMembers().size();
    } else {
      throw new IllegalArgumentException("해당 ID의 커뮤니티 게시글이 없습니다: " + postId);
    }
  }
}