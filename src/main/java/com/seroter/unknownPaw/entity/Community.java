package com.seroter.unknownPaw.entity;

import com.seroter.unknownPaw.dto.CommunityRequestDTO;
import com.seroter.unknownPaw.entity.Enum.CommunityCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "community")
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;

    private String title;
    private String content;

    private int likes;

    // 댓글 수 변수 이름 'comment' → 'commentCount'로 변경 (가독성 및 중복 방지)
    private int commentCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private CommunityCategory communityCategory;

    private LocalDateTime regDate;

    @PrePersist
    public void prePersist() {
        this.regDate = LocalDateTime.now();
    }

    public void modify(CommunityRequestDTO communityRequestDTO) {
        this.title = communityRequestDTO.getTitle();
        this.content = communityRequestDTO.getContent();
        this.communityCategory = communityRequestDTO.getCommunityCategory();
    }

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommunityImage> communityImages = new ArrayList<>();

    public void addImage(CommunityImage image) {
        communityImages.add(image);
        image.setCommunity(this);
    }

    public void removeImage(CommunityImage image) {
        communityImages.remove(image);
        image.setCommunity(null);
    }

    // 댓글 리스트 (기존 commentCount와 이름 충돌 방지)
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

}
