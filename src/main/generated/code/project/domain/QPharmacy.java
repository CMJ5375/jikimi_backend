package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPharmacy is a Querydsl query type for Pharmacy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPharmacy extends EntityPathBase<Pharmacy> {

    private static final long serialVersionUID = -333161241L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPharmacy pharmacy = new QPharmacy("pharmacy");

    public final QFacility facility;

    public final NumberPath<Long> pharmacyId = createNumber("pharmacyId", Long.class);

    public final StringPath pharmacyName = createString("pharmacyName");

    public QPharmacy(String variable) {
        this(Pharmacy.class, forVariable(variable), INITS);
    }

    public QPharmacy(Path<? extends Pharmacy> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPharmacy(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPharmacy(PathMetadata metadata, PathInits inits) {
        this(Pharmacy.class, metadata, inits);
    }

    public QPharmacy(Class<? extends Pharmacy> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.facility = inits.isInitialized("facility") ? new QFacility(forProperty("facility")) : null;
    }

}

