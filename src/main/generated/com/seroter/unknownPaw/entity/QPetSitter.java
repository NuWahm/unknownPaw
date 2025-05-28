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
    public final StringPath flexibleLocation;

    //inherited
    public final NumberPath<Integer> hourlyRate;

    //inherited
    public final ListPath<Image, QImage> images;

    //inherited
    public final NumberPath<Double> latitude;

    public final ListPath<String, StringPath> license = this.<String, StringPath>createList("license", String.class, StringPath.class, PathInits.DIRECT2);

    //inherited
    public final NumberPath<Integer> likes;

    //inherited
    public final NumberPath<Double> longitude;

    // inherited
    public final QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate;

    public final NumberPath<Integer> petExperience = createNumber("petExperience", Integer.class);

    //inherited
    public final NumberPath<Long> postId;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate;

    //inherited
    public final EnumPath<com.seroter.unknownPaw.entity.Enum.ServiceCategory> serviceCategory;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> serviceDate;

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
        this.flexibleLocation = _super.flexibleLocation;
        this.hourlyRate = _super.hourlyRate;
        this.images = _super.images;
        this.latitude = _super.latitude;
        this.likes = _super.likes;
        this.longitude = _super.longitude;
        this.member = _super.member;
        this.modDate = _super.modDate;
        this.postId = _super.postId;
        this.regDate = _super.regDate;
        this.serviceCategory = _super.serviceCategory;
        this.serviceDate = _super.serviceDate;
        this.title = _super.title;
    }

}

