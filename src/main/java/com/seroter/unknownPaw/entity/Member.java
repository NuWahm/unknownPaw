package com.seroter.unknownPaw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mid;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(nullable = false)
    private boolean fromSocial;

    @Column(length = 100)
    private String socialId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private int birthday;

    @Column(nullable = false)
    private Boolean gender;

    @Column(length = 255)
    private String address;

    private float pawRate;

    private String profileImagePath;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(length = 30)
    private String signupChannel;

    // === Enum ===
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    public enum Role {
        USER, ADMIN
    }

    public enum MemberStatus {
        ACTIVE, INACTIVE, BANNED, DELETED
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "member_roles",
            joinColumns = @JoinColumn(name = "member_id")
    )
    @Column(name = "role")
    @Builder.Default
    private Set<Role> roleSet = new HashSet<>();

    public void addRole(Role role) {
        roleSet.add(role);
    }

    public void addMemberRole(Role role) {
        this.roleSet.add(role);
    }

    // 👇 펫 리스트 연관관계
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Pet> pets = new HashSet<>();

    // introduce 소개
    @Column(length = 500)
    private String introduce;

    public List<Pet> getPets() {
        return new ArrayList<>(pets);
    }
    public String getIntroduce() { return this.introduce; }

    // 👇 좋아요 연관관계들
    @ManyToMany
    @JoinTable(
            name = "member_liked_petowner_posts",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @Builder.Default
    private Set<PetOwner> likedPetOwner = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "member_liked_petsitter_posts",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @Builder.Default
    private Set<PetSitter> likedPetSitter = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "member_liked_community_posts",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "community_id")
    )
    @Builder.Default
    private Set<Community> likedCommunity = new HashSet<>();
}