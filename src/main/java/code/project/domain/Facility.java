package code.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facility")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id")
    private Long facilityId;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FacilityType type;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 20)
    private String regionCode;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FacilityBusinessHour> businessHours = new ArrayList<>();

    @OneToOne(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Hospital hospital;   // 병원 역참조

    @OneToOne(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pharmacy pharmacy;   // 약국 역참조

    public void addBusinessHour(FacilityBusinessHour hour) {
        businessHours.add(hour);
        hour.setFacility(this);
    }

    public void removeBusinessHour(FacilityBusinessHour hour) {
        businessHours.remove(hour);
        hour.setFacility(null);
    }
}
