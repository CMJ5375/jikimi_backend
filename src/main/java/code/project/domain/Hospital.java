package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hospital")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hospitalId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    // 병원 이름
    @Column(nullable = false, length = 255)
    private String hospitalName;

    // 진료 시간
    @Column(length = 255)
    private String businessHour;

    // 응급실 유무
    @Column(nullable = false)
    private boolean hasEmergency = false;

    // 관계 설정
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HospitalDepartment> departments = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HospitalBusinessHour> businessHours = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HospitalInstitution> institutions = new ArrayList<>();
}
