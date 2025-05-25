package com.seroter.unknownPaw.dto;



import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityRequestDTO {
    private String title;                        // 글 제목
    private String content;                      // 글 내용
    private CommunityCategory communityCategory; // 커뮤니티 카테고리 (Enum 값으로 구분)
    private MultipartFile thumbnailImage;        // 썸네일 이미지 (대표 이미지)
    private List<MultipartFile> detailImages;    // 상세 이미지 리스트
}
