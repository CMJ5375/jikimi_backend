package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJPost is a Querydsl query type for JPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJPost extends EntityPathBase<JPost> {

    private static final long serialVersionUID = -1603224458L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJPost jPost = new QJPost("jPost");

    public final EnumPath<BoardCategory> boardCategory = createEnum("boardCategory", BoardCategory.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath fileUrl = createString("fileUrl");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final ListPath<JPostLike, QJPostLike> likes = this.<JPostLike, QJPostLike>createList("likes", JPostLike.class, QJPostLike.class, PathInits.DIRECT2);

    public final NumberPath<Long> postId = createNumber("postId", Long.class);

    public final StringPath title = createString("title");

    public final QJUser user;

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QJPost(String variable) {
        this(JPost.class, forVariable(variable), INITS);
    }

    public QJPost(Path<? extends JPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJPost(PathMetadata metadata, PathInits inits) {
        this(JPost.class, metadata, inits);
    }

    public QJPost(Class<? extends JPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QJUser(forProperty("user")) : null;
    }

}

