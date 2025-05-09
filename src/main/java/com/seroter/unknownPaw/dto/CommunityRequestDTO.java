package com.seroter.unknownPaw.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityRequestDTO {

    private String title;                     // 글 제목
    private String content;                   // 글 내용
    private String defaultLocation;           // 기본 위치
    private String flexibleLocation;          // 유동적인 위치

    private int desiredHourlyRate;            // 희망 시급
    private String serviceCategory;           // 서비스 카테고리 (Enum 문자열)
    private String postType;                  // POST 타입 (COMMUNITY로 고정)

    private MultipartFile thumbnailImage;     // 썸네일 이미지
    private List<MultipartFile> detailImages; // 상세 이미지 리스트

    private String communityCategory;         // 커뮤니티 카테고리 (Enum 문자열)
}
