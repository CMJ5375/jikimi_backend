package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHospitalDepartment is a Querydsl query type for HospitalDepartment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHospitalDepartment extends EntityPathBase<HospitalDepartment> {

    private static final long serialVersionUID = 151094368L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHospitalDepartment hospitalDepartment = new QHospitalDepartment("hospitalDepartment");

    public final StringPath departmentName = createString("departmentName");

    public final QHospital hospital;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QHospitalDepartment(String variable) {
        this(HospitalDepartment.class, forVariable(variable), INITS);
    }

    public QHospitalDepartment(Path<? extends HospitalDepartment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHospitalDepartment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHospitalDepartment(PathMetadata metadata, PathInits inits) {
        this(HospitalDepartment.class, metadata, inits);
    }

    public QHospitalDepartment(Class<? extends HospitalDepartment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hospital = inits.isInitialized("hospital") ? new QHospital(forProperty("hospital"), inits.get("hospital")) : null;
    }

}

