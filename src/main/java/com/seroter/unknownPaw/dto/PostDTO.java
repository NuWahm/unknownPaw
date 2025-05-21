package com.seroter.unknownPaw.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.seroter.unknownPaw.entity.Member;
import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PetSitter;
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

  private static final Logger log = LogManager.getLogger(PostDTO.class);

  private Long postId; // 글번호 (고유 키)
  private String title; // 글제목
  private String content; // 글내용
  private String serviceCategory; // 서비스 카테고리 (산책 , 호텔링 , 돌봄)
  private int hourlyRate; // 시급 (PetOn = 시급, PetSi = 희망 시급)
  private int likes;
  private LocalDateTime serviceDate;
  private int chatCount; // 채팅 수
  private String defaultLocation; // 기본 위치
  private String flexibleLocation; // 유동적인 위치
  private Double latitude;            // 위도
  private Double longitude;           // 경도
  private LocalDateTime regDate; //  등록일
  private LocalDateTime modDate; //  수정일

  private Long petId;           // PetOwner에 연결된 Pet ID

  private List<String> license;      // PetSitter: 자격증
  private Integer petExperience; // PetSitter: 경력(연차 등, null 허용)

//  private String email; // 작성자 이메일 (Members 엔티티 참조) 더 가져올 정보 多
  @JsonProperty("images")
  private List<ImageDTO> images;            // 게시글에 첨부된 이미지 리스트

  private boolean isPetSitterPost;          // true: 시터 글, false: 오너 글

  private MemberResponseDTO member;         // 작성자 정보 DTO

  /**
   * 무한 스크롤 구현을 위한 ID 반환 메서드
   */
  @Override
  public Long getId() {
    return this.postId;
  }

  /**
   * Post 엔티티 → PostDTO 로 변환하는 정적 팩토리 메서드
   */
  public static PostDTO fromEntity(Post post) {
    List<ImageDTO> images = Collections.emptyList();

    // 시터 게시글 또는 오너 게시글일 경우에 따라 이미지 처리
    if (post instanceof PetOwner owner && owner.getImages() != null) {
      images = owner.getImages().stream()
              .map(img -> ImageDTO.builder()
                      .imgId(img.getImgId())
                      .path(img.getPath())
                      .build())
              .collect(Collectors.toList());
    } else if (post instanceof PetSitter sitter && sitter.getImages() != null) {
      images = sitter.getImages().stream()
              .map(img -> ImageDTO.builder()
                      .imgId(img.getImgId())
                      .path(img.getPath())
                      .build())
              .collect(Collectors.toList());
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
            .images(images)                     // ← 여기!
            .isPetSitterPost(post instanceof PetSitter);


    // 작성자 정보 추가
    if (post.getMember() != null) {
      builder.member(fromEntity(post.getMember()));
    } else {
      log.warn("POST entity with ID {} has a null member.", post.getPostId());
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

