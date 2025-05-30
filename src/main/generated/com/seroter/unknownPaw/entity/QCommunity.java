package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommunity is a Querydsl query type for Community
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunity extends EntityPathBase<Community> {

    private static final long serialVersionUID = 2061311751L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommunity community = new QCommunity("community");

    public final NumberPath<Integer> comment = createNumber("comment", Integer.class);

    public final ListPath<Comment, QComment> comments = this.<Comment, QComment>createList("comments", Comment.class, QComment.class, PathInits.DIRECT2);

    public final EnumPath<com.seroter.unknownPaw.entity.Enum.CommunityCategory> communityCategory = createEnum("communityCategory", com.seroter.unknownPaw.entity.Enum.CommunityCategory.class);

    public final NumberPath<Long> communityId = createNumber("communityId", Long.class);

    public final ListPath<CommunityImage, QCommunityImage> communityImages = this.<CommunityImage, QCommunityImage>createList("communityImages", CommunityImage.class, QCommunityImage.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final ListPath<Member, QMember> likedMembers = this.<Member, QMember>createList("likedMembers", Member.class, QMember.class, PathInits.DIRECT2);

    public final NumberPath<Integer> likes = createNumber("likes", Integer.class);

    public final QMember member;

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    public QCommunity(String variable) {
        this(Community.class, forVariable(variable), INITS);
    }

    public QCommunity(Path<? extends Community> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommunity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommunity(PathMetadata metadata, PathInits inits) {
        this(Community.class, metadata, inits);
    }

    public QCommunity(Class<? extends Community> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

