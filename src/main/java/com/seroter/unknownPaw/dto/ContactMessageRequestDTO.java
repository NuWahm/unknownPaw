package com.seroter.unknownPaw.dto;

import lombok.Data;

@Data
public class ContactMessageRequestDTO {
    private String subject;   // 제목
    private String message;   // 내용
    private Long mid;    // 로그인한 사용자 ID (mid)
}
