package com.seroter.unknownPaw.entity.EscrowEntity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEscrowPayment is a Querydsl query type for EscrowPayment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEscrowPayment extends EntityPathBase<EscrowPayment> {

    private static final long serialVersionUID = 6748061L;

    public static final QEscrowPayment escrowPayment = new QEscrowPayment("escrowPayment");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final NumberPath<Long> ownerMid = createNumber("ownerMid", Long.class);

    public final DateTimePath<java.time.LocalDateTime> paidAt = createDateTime("paidAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> paymentId = createNumber("paymentId", Long.class);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> releasedAt = createDateTime("releasedAt", java.time.LocalDateTime.class);

    public final ListPath<ServiceProof, QServiceProof> serviceProofs = this.<ServiceProof, QServiceProof>createList("serviceProofs", ServiceProof.class, QServiceProof.class, PathInits.DIRECT2);

    public final NumberPath<Long> sitterMid = createNumber("sitterMid", Long.class);

    public final EnumPath<EscrowStatus> status = createEnum("status", EscrowStatus.class);

    public QEscrowPayment(String variable) {
        super(EscrowPayment.class, forVariable(variable));
    }

    public QEscrowPayment(Path<? extends EscrowPayment> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEscrowPayment(PathMetadata metadata) {
        super(EscrowPayment.class, metadata);
    }

}

