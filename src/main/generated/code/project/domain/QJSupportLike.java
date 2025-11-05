package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJSupportLike is a Querydsl query type for JSupportLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJSupportLike extends EntityPathBase<JSupportLike> {

    private static final long serialVersionUID = 356918128L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJSupportLike jSupportLike = new QJSupportLike("jSupportLike");

    public final QJSupport support;

    public final NumberPath<Long> supportLikeCount = createNumber("supportLikeCount", Long.class);

    public final QJUser user;

    public QJSupportLike(String variable) {
        this(JSupportLike.class, forVariable(variable), INITS);
    }

    public QJSupportLike(Path<? extends JSupportLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJSupportLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJSupportLike(PathMetadata metadata, PathInits inits) {
        this(JSupportLike.class, metadata, inits);
    }

    public QJSupportLike(Class<? extends JSupportLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.support = inits.isInitialized("support") ? new QJSupport(forProperty("support"), inits.get("support")) : null;
        this.user = inits.isInitialized("user") ? new QJUser(forProperty("user")) : null;
    }

}

