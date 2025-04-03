package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "pet_on") // 테이블 이름 설정
public class PetOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petOwnerId ; // 글번호 (고유 키)

    private String title; // 글제목

    @Column(columnDefinition = "TEXT")
    private String content; // 글내용

    @Enumerated(EnumType.STRING)
    private ServiceCategory category; // 서비스 카테고리 (산책, 호텔링, 돌봄)

    private int hourlyRate; // 시급

    private int likes; // 관심(좋아요 수)

    private int chatCount; // 채팅 개수

    private String defaultLocation; // 기본 위치

    private String flexibleLocation; // 유동적인 위치

    private LocalDateTime regDate; // 등록일

    private LocalDateTime modDate; // 수정일

    // 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid")
    private Members members; // 회원번호(참조 키) (펫오너)



//    public void changeTitle() {
//        this.changeTitle = title;
//    }
//    public void changeContent() {
//        this.changeContent = content;
//    }
}