package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJSupport is a Querydsl query type for JSupport
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJSupport extends EntityPathBase<JSupport> {

    private static final long serialVersionUID = 1208009785L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJSupport jSupport = new QJSupport("jSupport");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath fileName = createString("fileName");

    public final StringPath fileUrl = createString("fileUrl");

    public final QJUser JUser;

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final NumberPath<Long> originalId = createNumber("originalId", Long.class);

    public final BooleanPath pinnedCopy = createBoolean("pinnedCopy");

    public final NumberPath<Long> supportId = createNumber("supportId", Long.class);

    public final StringPath title = createString("title");

    public final StringPath type = createString("type");

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QJSupport(String variable) {
        this(JSupport.class, forVariable(variable), INITS);
    }

    public QJSupport(Path<? extends JSupport> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJSupport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJSupport(PathMetadata metadata, PathInits inits) {
        this(JSupport.class, metadata, inits);
    }

    public QJSupport(Class<? extends JSupport> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.JUser = inits.isInitialized("JUser") ? new QJUser(forProperty("JUser")) : null;
    }

}

