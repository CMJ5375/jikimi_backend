package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHospital is a Querydsl query type for Hospital
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHospital extends EntityPathBase<Hospital> {

    private static final long serialVersionUID = 1040193134L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHospital hospital = new QHospital("hospital");

    public final ListPath<HospitalDepartment, QHospitalDepartment> departments = this.<HospitalDepartment, QHospitalDepartment>createList("departments", HospitalDepartment.class, QHospitalDepartment.class, PathInits.DIRECT2);

    public final QFacility facility;

    public final BooleanPath hasEmergency = createBoolean("hasEmergency");

    public final NumberPath<Long> hospitalId = createNumber("hospitalId", Long.class);

    public final StringPath hospitalName = createString("hospitalName");

    public final ListPath<HospitalInstitution, QHospitalInstitution> institutions = this.<HospitalInstitution, QHospitalInstitution>createList("institutions", HospitalInstitution.class, QHospitalInstitution.class, PathInits.DIRECT2);

    public QHospital(String variable) {
        this(Hospital.class, forVariable(variable), INITS);
    }

    public QHospital(Path<? extends Hospital> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHospital(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHospital(PathMetadata metadata, PathInits inits) {
        this(Hospital.class, metadata, inits);
    }

    public QHospital(Class<? extends Hospital> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.facility = inits.isInitialized("facility") ? new QFacility(forProperty("facility")) : null;
    }

}

