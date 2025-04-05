package com.boilerplate.boilerplate.domain.post.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikedPost is a Querydsl query type for LikedPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikedPost extends EntityPathBase<LikedPost> {

    private static final long serialVersionUID = 451548445L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikedPost likedPost = new QLikedPost("likedPost");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPost post;

    public final com.boilerplate.boilerplate.domain.user.entity.QUser user;

    public QLikedPost(String variable) {
        this(LikedPost.class, forVariable(variable), INITS);
    }

    public QLikedPost(Path<? extends LikedPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikedPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikedPost(PathMetadata metadata, PathInits inits) {
        this(LikedPost.class, metadata, inits);
    }

    public QLikedPost(Class<? extends LikedPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QPost(forProperty("post"), inits.get("post")) : null;
        this.user = inits.isInitialized("user") ? new com.boilerplate.boilerplate.domain.user.entity.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

