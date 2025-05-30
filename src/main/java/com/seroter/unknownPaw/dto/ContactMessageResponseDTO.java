package com.seroter.unknownPaw.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ContactMessageResponseDTO {
    private Long messageId;
    private String subject;
    private String message;
    private LocalDateTime createdAt;

    private String memberName;
    private String memberEmail;

    private List<ContactAnswerDTO> answers;

    @Data
    @Builder
    public static class ContactAnswerDTO {
        private Long answerId;
        private String content;
        private String adminName;
        private LocalDateTime createdAt;
    }
}
