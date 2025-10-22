package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJComment is a Querydsl query type for JComment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJComment extends EntityPathBase<JComment> {

    private static final long serialVersionUID = -281791799L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJComment jComment = new QJComment("jComment");

    public final NumberPath<Long> commentId = createNumber("commentId", Long.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final QJPost JPost;

    public final QJUser JUser;

    public QJComment(String variable) {
        this(JComment.class, forVariable(variable), INITS);
    }

    public QJComment(Path<? extends JComment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJComment(PathMetadata metadata, PathInits inits) {
        this(JComment.class, metadata, inits);
    }

    public QJComment(Class<? extends JComment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.JPost = inits.isInitialized("JPost") ? new QJPost(forProperty("JPost"), inits.get("JPost")) : null;
        this.JUser = inits.isInitialized("JUser") ? new QJUser(forProperty("JUser")) : null;
    }

}

