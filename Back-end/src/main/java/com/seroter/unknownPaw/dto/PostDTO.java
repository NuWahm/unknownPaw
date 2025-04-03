package com.seroter.unknownPaw.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long postId; // 글번호 (고유 키)
    private String title; // 글제목
    private String content; // 글내용
    private String category; // 서비스 카테고리 (산책, 호텔링, 돌봄)
    private int hourlyRate; // 시급 (PetOn = 시급, PetSi = 희망 시급)
    private int likes; // 관심(좋아요 수)
    private int chatCount; // 채팅 수
    private String defaultLocation; // 기본 위치
    private String flexibleLocation; // 유동적인 위치
    private LocalDateTime regDate; // 등록일
    private LocalDateTime modDate; // 수정일
    private String writerEmail; // 작성자 이메일 (Members 엔티티 참조)
    private List<ImagesDTO> photos; // 업로드된 이미지 리스트
    private boolean isPetSitterPost; // true: PetSi 게시글, false: PetOn 게시글
}
