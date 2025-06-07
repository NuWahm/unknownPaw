package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId; // 문의글 고유 ID

    private String subject; // 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message; // 내용

    private LocalDateTime createdAt;

    // 작성자 (회원)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid")
    private Member member;

    // 답변 리스트 (1:N)
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactAnswer>answers = new ArrayList<>();

    @PrePersist
    public void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}
