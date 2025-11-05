package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJPostLike is a Querydsl query type for JPostLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJPostLike extends EntityPathBase<JPostLike> {

    private static final long serialVersionUID = 1213576493L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJPostLike jPostLike = new QJPostLike("jPostLike");

    public final QJPost post;

    public final NumberPath<Long> postLikeCount = createNumber("postLikeCount", Long.class);

    public final QJUser user;

    public QJPostLike(String variable) {
        this(JPostLike.class, forVariable(variable), INITS);
    }

    public QJPostLike(Path<? extends JPostLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJPostLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJPostLike(PathMetadata metadata, PathInits inits) {
        this(JPostLike.class, metadata, inits);
    }

    public QJPostLike(Class<? extends JPostLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.post = inits.isInitialized("post") ? new QJPost(forProperty("post"), inits.get("post")) : null;
        this.user = inits.isInitialized("user") ? new QJUser(forProperty("user")) : null;
    }

}

