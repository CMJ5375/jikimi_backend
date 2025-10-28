package code.project.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFacility is a Querydsl query type for Facility
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFacility extends EntityPathBase<Facility> {

    private static final long serialVersionUID = 1844938455L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFacility facility = new QFacility("facility");

    public final StringPath address = createString("address");

    public final ListPath<FacilityBusinessHour, QFacilityBusinessHour> businessHours = this.<FacilityBusinessHour, QFacilityBusinessHour>createList("businessHours", FacilityBusinessHour.class, QFacilityBusinessHour.class, PathInits.DIRECT2);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final QHospital hospital;

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    public final StringPath name = createString("name");

    public final QPharmacy pharmacy;

    public final StringPath phone = createString("phone");

    public final StringPath regionCode = createString("regionCode");

    public final EnumPath<FacilityType> type = createEnum("type", FacilityType.class);

    public QFacility(String variable) {
        this(Facility.class, forVariable(variable), INITS);
    }

    public QFacility(Path<? extends Facility> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFacility(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFacility(PathMetadata metadata, PathInits inits) {
        this(Facility.class, metadata, inits);
    }

    public QFacility(Class<? extends Facility> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hospital = inits.isInitialized("hospital") ? new QHospital(forProperty("hospital"), inits.get("hospital")) : null;
        this.pharmacy = inits.isInitialized("pharmacy") ? new QPharmacy(forProperty("pharmacy"), inits.get("pharmacy")) : null;
    }

}

