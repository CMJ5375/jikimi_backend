package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJUserFavorite is a Querydsl query type for JUserFavorite
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJUserFavorite extends EntityPathBase<JUserFavorite> {

    private static final long serialVersionUID = 24775773L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJUserFavorite jUserFavorite = new QJUserFavorite("jUserFavorite");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> favoriteId = createNumber("favoriteId", Long.class);

    public final QHospital hospital;

    public final QPharmacy pharmacy;

    public final EnumPath<FacilityType> type = createEnum("type", FacilityType.class);

    public final QJUser user;

    public QJUserFavorite(String variable) {
        this(JUserFavorite.class, forVariable(variable), INITS);
    }

    public QJUserFavorite(Path<? extends JUserFavorite> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJUserFavorite(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJUserFavorite(PathMetadata metadata, PathInits inits) {
        this(JUserFavorite.class, metadata, inits);
    }

    public QJUserFavorite(Class<? extends JUserFavorite> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hospital = inits.isInitialized("hospital") ? new QHospital(forProperty("hospital"), inits.get("hospital")) : null;
        this.pharmacy = inits.isInitialized("pharmacy") ? new QPharmacy(forProperty("pharmacy"), inits.get("pharmacy")) : null;
        this.user = inits.isInitialized("user") ? new QJUser(forProperty("user")) : null;
    }

}

