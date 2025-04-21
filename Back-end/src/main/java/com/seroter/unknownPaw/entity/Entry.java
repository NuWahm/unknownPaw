package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 📌 [Entity] 게시글 정보를 나타내는 JPA 엔티티
 * DB 테이블명: entry
 */
@Entity
@Table(name = "entry")
@Getter
@Setter
public class Entry {

    // 기본키 (자동 생성)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게시글 제목
    private String title;

    // 게시글 내용
    private String content;

    // 생성일시
    private LocalDateTime createdAt;

    // 저장 전 자동으로 현재 시간 설정
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
