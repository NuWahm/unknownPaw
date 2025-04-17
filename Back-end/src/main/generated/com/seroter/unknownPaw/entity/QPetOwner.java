package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPetOwner is a Querydsl query type for PetOwner
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPetOwner extends EntityPathBase<PetOwner> {

    private static final long serialVersionUID = -526720522L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPetOwner petOwner = new QPetOwner("petOwner");

    public final QPost _super;

    //inherited
    public final NumberPath<Integer> chatCount;

    //inherited
    public final StringPath content;

    //inherited
    public final StringPath defaultLocation;

    //inherited
    public final NumberPath<Integer> desiredHourlyRate;

    //inherited
    public final StringPath flexibleLocation;

    //inherited
    public final NumberPath<Integer> likes;

    // inherited
    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate;

    //inherited
    public final NumberPath<Long> postId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate;

    //inherited
    public final EnumPath<ServiceCategory> serviceCategory;

    //inherited
    public final StringPath title;

    public QPetOwner(String variable) {
        this(PetOwner.class, forVariable(variable), INITS);
    }

    public QPetOwner(Path<? extends PetOwner> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPetOwner(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPetOwner(PathMetadata metadata, PathInits inits) {
        this(PetOwner.class, metadata, inits);
    }

    public QPetOwner(Class<? extends PetOwner> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QPost(type, metadata, inits);
        this.chatCount = _super.chatCount;
        this.content = _super.content;
        this.defaultLocation = _super.defaultLocation;
        this.desiredHourlyRate = _super.desiredHourlyRate;
        this.flexibleLocation = _super.flexibleLocation;
        this.likes = _super.likes;
        this.member = _super.member;
        this.modDate = _super.modDate;
        this.postId = _super.postId;
        this.regDate = _super.regDate;
        this.serviceCategory = _super.serviceCategory;
        this.title = _super.title;
    }

}

