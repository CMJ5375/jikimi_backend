package code.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "facility_business_hour",
        indexes = {
                @Index(name = "idx_facility_business_hour_facility", columnList = "facility_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class FacilityBusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    //요일 콤마 처리
    @Column(name = "days", nullable = false, length = 50)
    private String days;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(name = "is_closed", nullable = false)
    @Builder.Default
    private boolean closed = false;

    @Column(name = "is_24h", nullable = false)
    @Builder.Default
    private boolean open24h = false;

    @Column(length = 255)
    private String note;
}
