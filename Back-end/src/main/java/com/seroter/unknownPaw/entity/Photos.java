package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"member", "pets", "petOn", "petSi"})
@Table(name = "photos")
public class Photos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imgId; // 고유 키

    private String profileImg; // 프로필 사진
    private String uuid; // UUID
    private String path; // 파일 경로

    @Column(nullable = false)
    private int role; // 1-멤버, 2-펫, 3-포스트

    // 회원정보 참조 (멤버)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid")
    private Members member;

    // 반려동물 정보 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private Pets pets;

    // 펫오너 포스트 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_owner_id")
    private PetOn petOn;

    // 펫시터 포스트 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_sitter_id")
    private PetSi petSi;
}
