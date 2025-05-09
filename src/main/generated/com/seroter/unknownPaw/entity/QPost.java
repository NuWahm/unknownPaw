package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPost is a Querydsl query type for Post
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QPost extends EntityPathBase<Post> {

    private static final long serialVersionUID = 508505794L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPost post = new QPost("post");

    public final NumberPath<Integer> chatCount = createNumber("chatCount", Integer.class);

    public final StringPath content = createString("content");

    public final StringPath defaultLocation = createString("defaultLocation");

    public final NumberPath<Integer> desiredHourlyRate = createNumber("desiredHourlyRate", Integer.class);

    public final StringPath flexibleLocation = createString("flexibleLocation");

    public final NumberPath<Integer> likes = createNumber("likes", Integer.class);

    public final QMember member;

    public final DateTimePath<java.time.LocalDateTime> modDate = createDateTime("modDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final EnumPath<PostType> postType = createEnum("postType", PostType.class);

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final EnumPath<ServiceCategory> serviceCategory = createEnum("serviceCategory", ServiceCategory.class);

    public final StringPath title = createString("title");

    public QPost(String variable) {
        this(Post.class, forVariable(variable), INITS);
    }

    public QPost(Path<? extends Post> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPost(PathMetadata metadata, PathInits inits) {
        this(Post.class, metadata, inits);
    }

    public QPost(Class<? extends Post> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

