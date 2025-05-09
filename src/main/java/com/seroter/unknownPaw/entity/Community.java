package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "community")
public class Community extends Post {

    // 썸네일 이미지 (1:1)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_img_id")
    private Image thumbnailImage;

    // 상세 이미지들 (1:N)
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityImage> images = new ArrayList<>();

    // 댓글들
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}
