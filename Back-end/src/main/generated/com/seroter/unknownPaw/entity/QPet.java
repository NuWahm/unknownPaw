package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPet is a Querydsl query type for Pet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPet extends EntityPathBase<Pet> {

    private static final long serialVersionUID = 1124781757L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPet pet = new QPet("pet");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath breed = createString("breed");

    public final NumberPath<Long> imgId = createNumber("imgId", Long.class);

    public final QMember member;

    public final DateTimePath<java.time.LocalDateTime> modDate = createDateTime("modDate", java.time.LocalDateTime.class);

    public final BooleanPath neutering = createBoolean("neutering");

    public final NumberPath<Integer> petBirth = createNumber("petBirth", Integer.class);

    public final BooleanPath petGender = createBoolean("petGender");

    public final NumberPath<Long> petId = createNumber("petId", Long.class);

    public final StringPath petIntroduce = createString("petIntroduce");

    public final StringPath petMbti = createString("petMbti");

    public final StringPath petName = createString("petName");

    public final NumberPath<Long> petOwnerId = createNumber("petOwnerId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final NumberPath<Double> weight = createNumber("weight", Double.class);

    public QPet(String variable) {
        this(Pet.class, forVariable(variable), INITS);
    }

    public QPet(Path<? extends Pet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPet(PathMetadata metadata, PathInits inits) {
        this(Pet.class, metadata, inits);
    }

    public QPet(Class<? extends Pet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

