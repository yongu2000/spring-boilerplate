package com.boilerplate.boilerplate.domain.auth.jwt.entity;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import javax.annotation.processing.Generated;


/**
 * QRefreshToken is a Querydsl query type for RefreshToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefreshToken extends EntityPathBase<RefreshToken> {

    private static final long serialVersionUID = -1212862065L;

    public static final QRefreshToken refreshToken1 = new QRefreshToken("refreshToken1");

    public final DateTimePath<java.time.LocalDateTime> expiration = createDateTime("expiration", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRefreshToken(String variable) {
        super(RefreshToken.class, forVariable(variable));
    }

    public QRefreshToken(Path<? extends RefreshToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRefreshToken(PathMetadata metadata) {
        super(RefreshToken.class, metadata);
    }

}

