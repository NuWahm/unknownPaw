package com.seroter.unknownPaw.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class ImageDTO {


    private Long imgId; // 이미지 ID
    private String profileImg; // 프로필 사진
    private String uuid; //  UUID
    private String path; //  파일 경로
    private String thumbnailPath;

    private int role; // 역할 (1- 멤버, 2- 펫, 3- 포스트)

    private Long memberId; // 회원 ID
    private Long petId; // 반려동물 ID
    private Long petOwnerId; // 펫오너 게시글 ID
    private Long petSitterId; // 펫시터 게시글 ID
}

