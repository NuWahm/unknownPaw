package com.seroter.unknownPaw.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1047519172L;

    public static final QMember member = new QMember("member1");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final StringPath address = createString("address");

    public final NumberPath<Integer> birthday = createNumber("birthday", Integer.class);

    public final StringPath email = createString("email");

    public final BooleanPath emailVerified = createBoolean("emailVerified");

    public final BooleanPath fromSocial = createBoolean("fromSocial");

    public final BooleanPath gender = createBoolean("gender");

    public final NumberPath<Long> mid = createNumber("mid", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modDate = _super.modDate;

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final NumberPath<Float> pawRate = createNumber("pawRate", Float.class);

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath profileImagePath = createString("profileImagePath");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regDate = _super.regDate;

    public final EnumPath<Member.Role> role = createEnum("role", Member.Role.class);

    public final SetPath<Member.Role, EnumPath<Member.Role>> roleSet = this.<Member.Role, EnumPath<Member.Role>>createSet("roleSet", Member.Role.class, EnumPath.class, PathInits.DIRECT2);

    public final StringPath signupChannel = createString("signupChannel");

    public final StringPath socialId = createString("socialId");

    public final EnumPath<Member.MemberStatus> status = createEnum("status", Member.MemberStatus.class);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

