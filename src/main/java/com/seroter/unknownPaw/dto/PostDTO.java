package com.seroter.unknownPaw.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seroter.unknownPaw.entity.Member;
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
//  private String email; // 작성자 이메일 (Members 엔티티 참조) 더 가져올 정보 多


  @JsonProperty("images")
  private List<ImageDTO> images; // 업로드된 이미지 리스트

  private boolean isPetSitterPost; // true: PetSitter 게시글, false: PetOwner 게시글

  private MemberResponseDTO member;
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
//        .email(post.getMember().getEmail())
        .images(images)                     // ← 여기!
        .isPetSitterPost(post instanceof PetSitter)
        .build();
  }
  // 정적 팩토리 사용 -- MemberResponseDTO 클래스 자체에 엔티티를 받아서 DTO 객체를 생성해 반환하는 정적 메서드를 만드는 방식
  public static MemberResponseDTO fromEntity(Member member) {
    if (member == null) {
      return null; // 또는 빈 DTO 반환 등 Null 처리 방식 결정
    }
    // 빌더를 사용하거나 Setter를 사용하여 DTO 객체 생성 및 반환
    return MemberResponseDTO.builder()
        .mid(member.getMid())
        .email(member.getEmail())
        .nickname(member.getNickname())
        .pawRate(member.getPawRate())
        .profileImagePath(member.getProfileImagePath()) // Member 엔티티에 필드가 있어야 함
        // 필요한 다른 필드 매핑
        .build();
  }

}

