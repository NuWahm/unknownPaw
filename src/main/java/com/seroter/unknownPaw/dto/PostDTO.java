package com.seroter.unknownPaw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seroter.unknownPaw.entity.PetOwner;
import com.seroter.unknownPaw.entity.PetSitter;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import com.seroter.unknownPaw.entity.Post;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO implements  Identifiable{



  private Long postId; // 글번호 (고유 키)

  private String title; // 글제목
  private String content; // 글내용
  private String serviceCategory; // 서비스 카테고리 (산책 , 호텔링 , 돌봄)
  private int hourlyRate; // 시급 (PetOn = 시급, PetSi = 희망 시급)
  private int likes; // 관심(좋아요 수)
  private int chatCount; // 채팅 수
  private String defaultLocation; // 기본 위치
  private String flexibleLocation; // 유동적인 위치
  private LocalDateTime regDate; //  등록일
  private LocalDateTime modDate; //  수정일
  private String email; // 작성자 이메일 (Members 엔티티 참조)

  @JsonProperty("images")
  private List<ImageDTO> images; // 업로드된 이미지 리스트

  private boolean isPetSitterPost; // true: PetSitter 게시글, false: PetOwner 게시글

  // 🖱️ 무한 스크롤
  @Override
  public Long getId() {
    return this.postId;
  }

  public static PostDTO fromEntity(Post post) {
    List<ImageDTO> images = List.of();  // 기본 빈 리스트


    if (post instanceof PetOwner owner) {
      images = owner.getImages().stream()
              .map(img -> ImageDTO.builder()
                      .imgId(img.getImgId())
                      .path(img.getPath())
                      .build())
              .toList();
    } else if (post instanceof PetSitter sitter) {
      images = sitter.getImages().stream()
              .map(img -> ImageDTO.builder()
                      .imgId(img.getImgId())
                      .path(img.getPath())
                      .build())
              .toList();
    }
    return PostDTO.builder()
            .postId(post.getPostId())
            .title(post.getTitle())
            .content(post.getContent())
            .serviceCategory(post.getServiceCategory().name())
            .hourlyRate(post.getDesiredHourlyRate())
            .likes(post.getLikes())
            .chatCount(post.getChatCount())
            .defaultLocation(post.getDefaultLocation())
            .flexibleLocation(post.getFlexibleLocation())
            .regDate(post.getRegDate())
            .modDate(post.getModDate())
            .email(post.getMember().getEmail())
            .images(images)                     // ← 여기!
            .isPetSitterPost(post instanceof PetSitter)
            .build();
  }

}


