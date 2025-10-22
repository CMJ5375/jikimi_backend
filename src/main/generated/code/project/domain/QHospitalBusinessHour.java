package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHospitalBusinessHour is a Querydsl query type for HospitalBusinessHour
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHospitalBusinessHour extends EntityPathBase<HospitalBusinessHour> {

    private static final long serialVersionUID = -561479854L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHospitalBusinessHour hospitalBusinessHour = new QHospitalBusinessHour("hospitalBusinessHour");

    public final StringPath closeTime = createString("closeTime");

    public final EnumPath<HospitalBusinessHour.DayOfWeek> dayOfWeek = createEnum("dayOfWeek", HospitalBusinessHour.DayOfWeek.class);

    public final QHospital hospital;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath openTime = createString("openTime");

    public QHospitalBusinessHour(String variable) {
        this(HospitalBusinessHour.class, forVariable(variable), INITS);
    }

    public QHospitalBusinessHour(Path<? extends HospitalBusinessHour> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHospitalBusinessHour(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHospitalBusinessHour(PathMetadata metadata, PathInits inits) {
        this(HospitalBusinessHour.class, metadata, inits);
    }

    public QHospitalBusinessHour(Class<? extends HospitalBusinessHour> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hospital = inits.isInitialized("hospital") ? new QHospital(forProperty("hospital"), inits.get("hospital")) : null;
    }

}

