package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPetSitter is a Querydsl query type for PetSitter
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPetSitter extends EntityPathBase<PetSitter> {

    private static final long serialVersionUID = 953313184L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPetSitter petSitter = new QPetSitter("petSitter");

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

    public final ListPath<Image, QImage> images = this.<Image, QImage>createList("images", Image.class, QImage.class, PathInits.DIRECT2);

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

    public QPetSitter(String variable) {
        this(PetSitter.class, forVariable(variable), INITS);
    }

    public QPetSitter(Path<? extends PetSitter> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPetSitter(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPetSitter(PathMetadata metadata, PathInits inits) {
        this(PetSitter.class, metadata, inits);
    }

    public QPetSitter(Class<? extends PetSitter> type, PathMetadata metadata, PathInits inits) {
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

