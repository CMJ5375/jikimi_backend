package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJNotice is a Querydsl query type for JNotice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJNotice extends EntityPathBase<JNotice> {

    private static final long serialVersionUID = 1137319214L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJNotice jNotice = new QJNotice("jNotice");

    public final StringPath content = createString("content");

    public final StringPath fileUrl = createString("fileUrl");

    public final QJUser JUser;

    public final NumberPath<Integer> likeCount = createNumber("likeCount", Integer.class);

    public final NumberPath<Long> noticeId = createNumber("noticeId", Long.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QJNotice(String variable) {
        this(JNotice.class, forVariable(variable), INITS);
    }

    public QJNotice(Path<? extends JNotice> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJNotice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJNotice(PathMetadata metadata, PathInits inits) {
        this(JNotice.class, metadata, inits);
    }

    public QJNotice(Class<? extends JNotice> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.JUser = inits.isInitialized("JUser") ? new QJUser(forProperty("JUser")) : null;
    }

}

