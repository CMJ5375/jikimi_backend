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

    public static final QFacility facility = new QFacility("facility");

    public final StringPath address = createString("address");

    public final ListPath<FacilityBusinessHour, QFacilityBusinessHour> businessHours = this.<FacilityBusinessHour, QFacilityBusinessHour>createList("businessHours", FacilityBusinessHour.class, QFacilityBusinessHour.class, PathInits.DIRECT2);

    public final NumberPath<Long> facilityId = createNumber("facilityId", Long.class);

    public final NumberPath<java.math.BigDecimal> latitude = createNumber("latitude", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> longitude = createNumber("longitude", java.math.BigDecimal.class);

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final StringPath regionCode = createString("regionCode");

    public final EnumPath<FacilityType> type = createEnum("type", FacilityType.class);

    public QFacility(String variable) {
        super(Facility.class, forVariable(variable));
    }

    public QFacility(Path<? extends Facility> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFacility(PathMetadata metadata) {
        super(Facility.class, metadata);
    }

}

