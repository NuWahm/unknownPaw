package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDateAppoint is a Querydsl query type for DateAppoint
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDateAppoint extends EntityPathBase<DateAppoint> {

    private static final long serialVersionUID = -2066909487L;

    public static final QDateAppoint dateAppoint = new QDateAppoint("dateAppoint");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath chat = createString("chat");

    public final DateTimePath<java.time.LocalDateTime> confirmationDate = createDateTime("confirmationDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> decideHourRate = createNumber("decideHourRate", Integer.class);

    public final StringPath defaultLocation = createString("defaultLocation");

    public final StringPath flexibleLocation = createString("flexibleLocation");

    public final DateTimePath<java.time.LocalDateTime> futureDate = createDateTime("futureDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> imgId = createNumber("imgId", Long.class);

    public final NumberPath<Long> mid = createNumber("mid", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final NumberPath<Long> petId = createNumber("petId", Long.class);

    public final NumberPath<Long> petOwnerId = createNumber("petOwnerId", Long.class);

    public final NumberPath<Long> petSitterId = createNumber("petSitterId", Long.class);

    public final BooleanPath readTheOriginalText = createBoolean("readTheOriginalText");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final BooleanPath reservationStatus = createBoolean("reservationStatus");

    public final NumberPath<Long> rno = createNumber("rno", Long.class);

    public final EnumPath<ServiceCategory> serviceCategory = createEnum("serviceCategory", ServiceCategory.class);

    public QDateAppoint(String variable) {
        super(DateAppoint.class, forVariable(variable));
    }

    public QDateAppoint(Path<? extends DateAppoint> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDateAppoint(PathMetadata metadata) {
        super(DateAppoint.class, metadata);
    }

}

