package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHospitalInstitution is a Querydsl query type for HospitalInstitution
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHospitalInstitution extends EntityPathBase<HospitalInstitution> {

    private static final long serialVersionUID = 756975210L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHospitalInstitution hospitalInstitution = new QHospitalInstitution("hospitalInstitution");

    public final QHospital hospital;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath resourceName = createString("resourceName");

    public QHospitalInstitution(String variable) {
        this(HospitalInstitution.class, forVariable(variable), INITS);
    }

    public QHospitalInstitution(Path<? extends HospitalInstitution> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHospitalInstitution(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHospitalInstitution(PathMetadata metadata, PathInits inits) {
        this(HospitalInstitution.class, metadata, inits);
    }

    public QHospitalInstitution(Class<? extends HospitalInstitution> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hospital = inits.isInitialized("hospital") ? new QHospital(forProperty("hospital"), inits.get("hospital")) : null;
    }

}

