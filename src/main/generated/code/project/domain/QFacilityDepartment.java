package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFacilityDepartment is a Querydsl query type for FacilityDepartment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFacilityDepartment extends EntityPathBase<FacilityDepartment> {

    private static final long serialVersionUID = 272254345L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFacilityDepartment facilityDepartment = new QFacilityDepartment("facilityDepartment");

    public final StringPath departmentName = createString("departmentName");

    public final QFacility facility;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QFacilityDepartment(String variable) {
        this(FacilityDepartment.class, forVariable(variable), INITS);
    }

    public QFacilityDepartment(Path<? extends FacilityDepartment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFacilityDepartment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFacilityDepartment(PathMetadata metadata, PathInits inits) {
        this(FacilityDepartment.class, metadata, inits);
    }

    public QFacilityDepartment(Class<? extends FacilityDepartment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.facility = inits.isInitialized("facility") ? new QFacility(forProperty("facility")) : null;
    }

}

