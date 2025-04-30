package com.seroter.unknownPaw.entity.EscrowEntity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QServiceProof is a Querydsl query type for ServiceProof
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QServiceProof extends EntityPathBase<ServiceProof> {

    private static final long serialVersionUID = 1357709939L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QServiceProof serviceProof = new QServiceProof("serviceProof");

    public final QEscrowPayment escrowPayment;

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath photoPath = createString("photoPath");

    public final NumberPath<Long> proofId = createNumber("proofId", Long.class);

    public final EnumPath<ProofStatus> proofStatus = createEnum("proofStatus", ProofStatus.class);

    public final DateTimePath<java.time.LocalDateTime> submittedAt = createDateTime("submittedAt", java.time.LocalDateTime.class);

    public QServiceProof(String variable) {
        this(ServiceProof.class, forVariable(variable), INITS);
    }

    public QServiceProof(Path<? extends ServiceProof> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QServiceProof(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QServiceProof(PathMetadata metadata, PathInits inits) {
        this(ServiceProof.class, metadata, inits);
    }

    public QServiceProof(Class<? extends ServiceProof> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.escrowPayment = inits.isInitialized("escrowPayment") ? new QEscrowPayment(forProperty("escrowPayment")) : null;
    }

}

