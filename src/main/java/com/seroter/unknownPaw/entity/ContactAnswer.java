package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId; // 답변 고유 ID

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 답변 내용

    private LocalDateTime createdAt;

    // 어떤 문의글에 대한 답변인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private ContactMessage message;

    // 답변한 관리자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Member admin;

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}
