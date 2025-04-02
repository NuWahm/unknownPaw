package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PetSi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petSitterId  ; // 글번호 (고유 키)

    private String stitle; // 글제목

    @Column(columnDefinition = "TEXT")
    private String content; // 글내용

    @Enumerated(EnumType.STRING)
    private ServiceCategory category; // 서비스 카테고리 (산책, 호텔링, 돌봄)

    private int desiredHourlyRate; // 희망 시급

    private int likes; // 관심(좋아요 수)

    private int chatCount; // 채팅 수

    private String defaultLocation; // 기본 위치

    private String flexibleLocation; // 유동적인 위치

    private LocalDateTime regDate; // 등록일

    private LocalDateTime modDate; // 수정일

    // 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid")
    private Members member; // 회원번호(참조 키) - 펫시터

    @OneToMany(mappedBy = "sitterPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photos> photos = new ArrayList<>(); // 사진번호(참조 키)

    @OneToMany(mappedBy = "sitterPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comments> comments = new ArrayList<>(); // 댓글번호(참조 키)
}
