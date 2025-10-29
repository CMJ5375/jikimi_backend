package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hospital",
        indexes = {
                @Index(name = "idx_hospital_facility", columnList = "facility_id"),
                @Index(name = "idx_hospital_has_emergency", columnList = "has_emergency")
        })
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "facility")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id")
    private Long hospitalId; // 별도 PK (Facility와 1:1 FK)

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false, unique = true)
    private Facility facility; // Facility 1:1 (공유 PK 아님)

    @Column(name = "hospital_name", length = 200, nullable = false)
    private String hospitalName;

    @Column(name = "org_type", length = 50)
    private String orgType;

    @Column(name = "has_emergency", nullable = false)
    private Boolean hasEmergency;

    // CSV로 합쳐 저장 (콤마 구분)
    @Column(name = "departments_csv", columnDefinition = "TEXT")
    private String departmentsCsv;   // 예: "내과,외과,소아청소년과"

    @Column(name = "institutions_csv", columnDefinition = "TEXT")
    private String institutionsCsv;  // 예: "MRI,CT,초음파"
}
