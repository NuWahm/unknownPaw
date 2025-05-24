package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDateAppoint is a Querydsl query type for DateAppoint
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDateAppoint extends EntityPathBase<DateAppoint> {

    private static final long serialVersionUID = -2066909487L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDateAppoint dateAppoint = new QDateAppoint("dateAppoint");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath chat = createString("chat");

    public final DateTimePath<java.time.LocalDateTime> confirmationDate = createDateTime("confirmationDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> decideHourRate = createNumber("decideHourRate", Integer.class);

    public final StringPath defaultLocation = createString("defaultLocation");

    public final StringPath flexibleLocation = createString("flexibleLocation");

    public final DateTimePath<java.time.LocalDateTime> futureDate = createDateTime("futureDate", java.time.LocalDateTime.class);

    public final QImage imgId;

    public final QMember mid;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final QPet petId;

    public final QPetOwner petOwnerPost;

    public final QPetSitter petSitterPost;

    public final BooleanPath readTheOriginalText = createBoolean("readTheOriginalText");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final BooleanPath reservationStatus = createBoolean("reservationStatus");

    public final NumberPath<Long> rno = createNumber("rno", Long.class);

    public final EnumPath<com.seroter.unknownPaw.entity.Enum.ServiceCategory> serviceCategory = createEnum("serviceCategory", com.seroter.unknownPaw.entity.Enum.ServiceCategory.class);

    public QDateAppoint(String variable) {
        this(DateAppoint.class, forVariable(variable), INITS);
    }

    public QDateAppoint(Path<? extends DateAppoint> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDateAppoint(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDateAppoint(PathMetadata metadata, PathInits inits) {
        this(DateAppoint.class, metadata, inits);
    }

    public QDateAppoint(Class<? extends DateAppoint> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.imgId = inits.isInitialized("imgId") ? new QImage(forProperty("imgId"), inits.get("imgId")) : null;
        this.mid = inits.isInitialized("mid") ? new QMember(forProperty("mid")) : null;
        this.petId = inits.isInitialized("petId") ? new QPet(forProperty("petId"), inits.get("petId")) : null;
        this.petOwnerPost = inits.isInitialized("petOwnerPost") ? new QPetOwner(forProperty("petOwnerPost"), inits.get("petOwnerPost")) : null;
        this.petSitterPost = inits.isInitialized("petSitterPost") ? new QPetSitter(forProperty("petSitterPost"), inits.get("petSitterPost")) : null;
    }

}

