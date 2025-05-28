package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QContactAnswer is a Querydsl query type for ContactAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContactAnswer extends EntityPathBase<ContactAnswer> {

    private static final long serialVersionUID = -1628313572L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContactAnswer contactAnswer = new QContactAnswer("contactAnswer");

    public final QMember admin;

    public final NumberPath<Long> answerId = createNumber("answerId", Long.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QContactMessage message;

    public QContactAnswer(String variable) {
        this(ContactAnswer.class, forVariable(variable), INITS);
    }

    public QContactAnswer(Path<? extends ContactAnswer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QContactAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QContactAnswer(PathMetadata metadata, PathInits inits) {
        this(ContactAnswer.class, metadata, inits);
    }

    public QContactAnswer(Class<? extends ContactAnswer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.admin = inits.isInitialized("admin") ? new QMember(forProperty("admin")) : null;
        this.message = inits.isInitialized("message") ? new QContactMessage(forProperty("message"), inits.get("message")) : null;
    }

}

