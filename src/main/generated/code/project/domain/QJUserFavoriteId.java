package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QJUserFavoriteId is a Querydsl query type for JUserFavoriteId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QJUserFavoriteId extends BeanPath<JUserFavoriteId> {

    private static final long serialVersionUID = -1960283560L;

    public static final QJUserFavoriteId jUserFavoriteId = new QJUserFavoriteId("jUserFavoriteId");

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QJUserFavoriteId(String variable) {
        super(JUserFavoriteId.class, forVariable(variable));
    }

    public QJUserFavoriteId(Path<? extends JUserFavoriteId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJUserFavoriteId(PathMetadata metadata) {
        super(JUserFavoriteId.class, metadata);
    }

}

