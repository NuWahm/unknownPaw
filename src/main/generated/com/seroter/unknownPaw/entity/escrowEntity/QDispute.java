package com.seroter.unknownPaw.entity.escrowEntity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDispute is a Querydsl query type for Dispute
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDispute extends EntityPathBase<Dispute> {

    private static final long serialVersionUID = 1224557376L;

    public static final QDispute dispute = new QDispute("dispute");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> disputeId = createNumber("disputeId", Long.class);

    public final NumberPath<Long> escrowPaymentId = createNumber("escrowPaymentId", Long.class);

    public final NumberPath<Long> ownerMid = createNumber("ownerMid", Long.class);

    public final StringPath reason = createString("reason");

    public final NumberPath<Long> sitterMid = createNumber("sitterMid", Long.class);

    public QDispute(String variable) {
        super(Dispute.class, forVariable(variable));
    }

    public QDispute(Path<? extends Dispute> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDispute(PathMetadata metadata) {
        super(Dispute.class, metadata);
    }

}

