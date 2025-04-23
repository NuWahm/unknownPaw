package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QImage is a Querydsl query type for Image
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImage extends EntityPathBase<Image> {

    private static final long serialVersionUID = -1422731399L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QImage image = new QImage("image");

    public final NumberPath<Long> imgId = createNumber("imgId", Long.class);

    public final QMember member;

    public final StringPath path = createString("path");

    public final QPet pet;

    public final QPetOwner petOwner;

    public final QPetSitter petSitter;

    public final StringPath profileImg = createString("profileImg");

    public final NumberPath<Integer> role = createNumber("role", Integer.class);

    public final StringPath uuid = createString("uuid");

    public QImage(String variable) {
        this(Image.class, forVariable(variable), INITS);
    }

    public QImage(Path<? extends Image> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QImage(PathMetadata metadata, PathInits inits) {
        this(Image.class, metadata, inits);
    }

    public QImage(Class<? extends Image> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
        this.pet = inits.isInitialized("pet") ? new QPet(forProperty("pet"), inits.get("pet")) : null;
        this.petOwner = inits.isInitialized("petOwner") ? new QPetOwner(forProperty("petOwner"), inits.get("petOwner")) : null;
        this.petSitter = inits.isInitialized("petSitter") ? new QPetSitter(forProperty("petSitter"), inits.get("petSitter")) : null;
    }

}

