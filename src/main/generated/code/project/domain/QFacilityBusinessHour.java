package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFacilityBusinessHour is a Querydsl query type for FacilityBusinessHour
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFacilityBusinessHour extends EntityPathBase<FacilityBusinessHour> {

    private static final long serialVersionUID = -90858949L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFacilityBusinessHour facilityBusinessHour = new QFacilityBusinessHour("facilityBusinessHour");

    public final BooleanPath closed = createBoolean("closed");

    public final TimePath<java.time.LocalTime> closeTime = createTime("closeTime", java.time.LocalTime.class);

    public final EnumPath<Bizday> dayOfWeek = createEnum("dayOfWeek", Bizday.class);

    public final QFacility facility;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath note = createString("note");

    public final BooleanPath open24h = createBoolean("open24h");

    public final TimePath<java.time.LocalTime> openTime = createTime("openTime", java.time.LocalTime.class);

    public QFacilityBusinessHour(String variable) {
        this(FacilityBusinessHour.class, forVariable(variable), INITS);
    }

    public QFacilityBusinessHour(Path<? extends FacilityBusinessHour> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFacilityBusinessHour(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFacilityBusinessHour(PathMetadata metadata, PathInits inits) {
        this(FacilityBusinessHour.class, metadata, inits);
    }

    public QFacilityBusinessHour(Class<? extends FacilityBusinessHour> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.facility = inits.isInitialized("facility") ? new QFacility(forProperty("facility")) : null;
    }

}

