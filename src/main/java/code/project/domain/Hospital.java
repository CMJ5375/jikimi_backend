package code.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospital")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hospitalId;

    // PK 공유 1:1을 원하면 @MapsId 패턴으로 바꾸고 @GeneratedValue 제거해야 합니다.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(nullable = false, length = 255)
    private String hospitalName;

    @Column(nullable = false)
    @Builder.Default
    private boolean hasEmergency = false;

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HospitalDepartment> departments = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HospitalInstitution> institutions = new ArrayList<>();
}
