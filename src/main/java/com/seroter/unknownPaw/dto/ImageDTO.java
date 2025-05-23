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
    private String profileImg; // 이미지 파일명
    private String uuid; // UUID
    private String path; // 저장 경로
    private int role; // 역할 (1-멤버, 2-펫, 3-게시글, 4-커뮤니티)

    private Long memberId; // 회원 ID (1)
    private Long petId; // 반려동물 ID (2)
    private Long petOwnerId; // 펫오너 게시글 ID (3)
    private Long petSitterId; // 펫시터 게시글 ID (3)
    private Long communityId; //  커뮤니티 게시글 ID (4)
}

