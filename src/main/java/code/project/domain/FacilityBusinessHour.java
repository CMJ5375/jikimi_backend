package code.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "facility_business_hour",
        indexes = {
                @Index(name = "idx_facility_business_hour_facility", columnList = "facility_id"),
                @Index(name = "idx_facility_business_hour_day", columnList = "day_of_week")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class FacilityBusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Facility 기준으로 귀속
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private Bizday dayOfWeek;

    // 가급적 문자열 대신 시간 타입 권장
    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    // 예외 상황(휴무/24시간 등) 표시용 플래그(선택)
    @Column(name = "is_closed", nullable = false)
    @Builder.Default
    private boolean closed = false;

    @Column(name = "is_24h", nullable = false)
    @Builder.Default
    private boolean open24h = false;

    @Column(length = 255)
    private String note; // 점심시간/특이사항 등 선택


}
