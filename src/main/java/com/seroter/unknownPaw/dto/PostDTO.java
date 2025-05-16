package com.seroter.unknownPaw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PetSitter;
import com.seroter.unknownPaw.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO implements Identifiable {

  private static final Logger log = LogManager.getLogger(PostDTO.class);

  private Long postId; // 글번호 (고유 키)
  private String title; // 글제목
  private String content; // 글내용
  private String serviceCategory; // 서비스 카테고리 (산책, 호텔링, 돌봄)
  private int likes; // 관심(좋아요 수)
  private int chatCount; // 채팅 수
  private String defaultLocation; // 기본 위치
  private String flexibleLocation; // 유동적인 위치
  private LocalDateTime regDate; // 등록일
  private LocalDateTime modDate; // 수정일

  @JsonProperty("images")
  private List<ImageDTO> images; // 업로드된 이미지 리스트

  private boolean isPetSitterPost; // true: PetSitter 게시글, false: PetOwner 게시글
  private int hourlyRate; // 시급 또는 희망시급 → 공통 DTO 필드로 관리
  private MemberResponseDTO member;

  // 🖱️ 무한 스크롤
  @Override
  public Long getId() {
    return this.postId;
  }

  public static PostDTO fromEntity(Post post) {
    List<ImageDTO> images = List.of();

    if (post instanceof PetOwner owner && owner.getImages() != null) {
      images = owner.getImages().stream()
              .map(img -> ImageDTO.builder()
                      .imgId(img.getImgId())
                      .path(img.getPath())
                      .build())
              .toList();
    } else if (post instanceof PetSitter sitter && sitter.getImages() != null) {
      images = sitter.getImages().stream()
              .map(img -> ImageDTO.builder()
                      .imgId(img.getImgId())
                      .path(img.getPath())
                      .build())
              .toList();
    }

    PostDTO.PostDTOBuilder builder = PostDTO.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .content(post.getContent())
            .serviceCategory(post.getServiceCategory().name())
            .likes(post.getLikes())
            .chatCount(post.getChatCount())
            .defaultLocation(post.getDefaultLocation())
            .flexibleLocation(post.getFlexibleLocation())
            .regDate(post.getRegDate())
            .modDate(post.getModDate())
            .images(images)
            .isPetSitterPost(post instanceof PetSitter)
            // PetSitter는 desiredHourlyRate를, PetOwner는 hourlyRate를 사용
            .hourlyRate(post.getHourlyRate());

    if (post.getMember() != null) {
      builder.member(MemberResponseDTO.builder()
              .mid(post.getMember().getMid())
              .email(post.getMember().getEmail())
              .nickname(post.getMember().getNickname())
              .pawRate(post.getMember().getPawRate())
              .profileImagePath(post.getMember().getProfileImagePath())
              .build());
    } else {
      log.warn("POST entity with ID {} has a null member.", post.getPostId());
    }

    return builder.build();
  }
}
