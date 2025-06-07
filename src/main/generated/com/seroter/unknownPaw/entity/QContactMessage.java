package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QContactMessage is a Querydsl query type for ContactMessage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QContactMessage extends EntityPathBase<ContactMessage> {

    private static final long serialVersionUID = -1430756503L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContactMessage contactMessage = new QContactMessage("contactMessage");

    public final ListPath<ContactAnswer, QContactAnswer> answers = this.<ContactAnswer, QContactAnswer>createList("answers", ContactAnswer.class, QContactAnswer.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QMember member;

    public final StringPath message = createString("message");

    public final NumberPath<Long> messageId = createNumber("messageId", Long.class);

    public final StringPath subject = createString("subject");

    public QContactMessage(String variable) {
        this(ContactMessage.class, forVariable(variable), INITS);
    }

    public QContactMessage(Path<? extends ContactMessage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QContactMessage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QContactMessage(PathMetadata metadata, PathInits inits) {
        this(ContactMessage.class, metadata, inits);
    }

    public QContactMessage(Class<? extends ContactMessage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

