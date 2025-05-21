package com.seroter.unknownPaw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.seroter.unknownPaw.entity.Enum.PostType;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PetSitter;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import com.seroter.unknownPaw.entity.Post;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 정보를 클라이언트에 전달하기 위한 DTO 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO implements Identifiable {



  private Long postId;                      // 글번호 (고유 키)
  private String title;                     // 글 제목
  private String content;                   // 글 내용
  private String serviceCategory;           // 서비스 카테고리 (산책, 호텔링, 돌봄)
  private int hourlyRate;                   // 시급
  private int likes;                        // 관심(좋아요 수)
  private int chatCount;                    // 채팅 수
  private String defaultLocation;           // 기본 위치
  private String flexibleLocation;          // 유동적인 위치
  private Double latitude;            // 위도
  private Double longitude;           // 경도
  private LocalDateTime regDate;            // 등록일
  private LocalDateTime modDate;            // 수정일


  @JsonProperty("images")
  private List<ImageDTO> images;            // 게시글에 첨부된 이미지 리스트
  private boolean isPetSitterPost;          // true: 시터 글, false: 오너 글
  private MemberResponseDTO member;         // 작성자 정보 DTO
  private String postTypeUrlSegment; // 예: "petowner" 또는 "petsitter"

  // 🖱️ 무한 스크롤

  @Override
  public Long getId() {
    return this.postId;
  }

  /**
   * Post 엔티티 → PostDTO 로 변환하는 정적 팩토리 메서드
   */
  public static PostDTO fromEntity(Post post) {

    List<ImageDTO> images = List.of();  // 기본 빈 리스트
// ✨ postType Enum 결정 (엔티티 타입 확인)
    PostType postTypeEnum = null;
    if (post instanceof PetSitter) {
      postTypeEnum = PostType.PET_SITTER;
    } else if (post instanceof PetOwner) {
      postTypeEnum = PostType.PET_OWNER;
    }
    String postTypeSegment = (postTypeEnum != null) ? postTypeEnum.getValue() : null;


    if (post instanceof PetOwner owner) {
      if (owner.getImages() != null) {
        images = owner.getImages().stream()
            .map(img -> ImageDTO.builder()
                .imgId(img.getImgId())
                .path(img.getPath())
                .build())
            .toList();
      }
    } else if (post instanceof PetSitter sitter) {
      if (sitter.getImages() != null) {
        images = sitter.getImages().stream()
            .map(img -> ImageDTO.builder()
                .imgId(img.getImgId())
                .path(img.getPath())
                .build())
            .toList();
      }

    }

    // DTO 빌더를 사용하여 객체 생성
    PostDTO.PostDTOBuilder builder = PostDTO.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .content(post.getContent())
            .serviceCategory(post.getServiceCategory().name())
            .hourlyRate(post.getHourlyRate())
            .likes(post.getLikes())
            .chatCount(post.getChatCount())
            .defaultLocation(post.getDefaultLocation())
            .flexibleLocation(post.getFlexibleLocation())
            .latitude(post.getLatitude())
            .longitude(post.getLongitude())
            .regDate(post.getRegDate())
            .modDate(post.getModDate())
            .images(images)
            .isPetSitterPost(post instanceof PetSitter); // 시터 게시글 여부 설정


    // 작성자 정보 추가
    if (post.getMember() != null) {

      Member memberEntity = post.getMember();

      MemberResponseDTO memberResponseDTO = MemberResponseDTO.builder()
          .mid(memberEntity.getMid())
          .email(memberEntity.getEmail())
          .nickname(memberEntity.getNickname())
          .profileImagePath(memberEntity.getProfileImagePath())
          .pawRate(memberEntity.getPawRate())
          .build();
      // MemberResponseDTO 객체를 설정하는 핵심
      builder.member(memberResponseDTO);

    } else {
      log.warn("POST entity with ID {} has a null member.", post.getPostId());
      builder.member(null);
    }

    return builder.build();
  }

  /**
   * Member 엔티티 → MemberResponseDTO 변환 메서드
   */
  public static MemberResponseDTO fromEntity(Member member) {
    if (member == null) {
      return null;
    }

    return MemberResponseDTO.builder()

            .mid(member.getMid())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .pawRate(member.getPawRate())
            .profileImagePath(member.getProfileImagePath())
            .build();

  }
}

