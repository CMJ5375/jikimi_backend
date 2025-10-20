package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserFavoriteId is a Querydsl query type for UserFavoriteId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QUserFavoriteId extends BeanPath<UserFavoriteId> {

    private static final long serialVersionUID = -1208368330L;

    public static final QUserFavoriteId userFavoriteId = new QUserFavoriteId("userFavoriteId");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserFavoriteId(String variable) {
        super(UserFavoriteId.class, forVariable(variable));
    }

    public QUserFavoriteId(Path<? extends UserFavoriteId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserFavoriteId(PathMetadata metadata) {
        super(UserFavoriteId.class, metadata);
    }

}

