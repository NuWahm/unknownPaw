package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommunityImage is a Querydsl query type for CommunityImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommunityImage extends EntityPathBase<CommunityImage> {

    private static final long serialVersionUID = -1113069932L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommunityImage communityImage = new QCommunityImage("communityImage");

    public final QCommunity community;

    public final NumberPath<Long> communityImageId = createNumber("communityImageId", Long.class);

    public final StringPath communityImageUrl = createString("communityImageUrl");

    public final BooleanPath communityIsThumbnail = createBoolean("communityIsThumbnail");

    public QCommunityImage(String variable) {
        this(CommunityImage.class, forVariable(variable), INITS);
    }

    public QCommunityImage(Path<? extends CommunityImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommunityImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommunityImage(PathMetadata metadata, PathInits inits) {
        this(CommunityImage.class, metadata, inits);
    }

    public QCommunityImage(Class<? extends CommunityImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.community = inits.isInitialized("community") ? new QCommunity(forProperty("community"), inits.get("community")) : null;
    }

}

