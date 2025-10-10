package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Hospital")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "facility")
public class Hospital {

    @Id
    private Long hospitalId;// PK = FK(Facility.facilityId) //병원 ID

    // Facility와 1:1, PK 공유
    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "hospital_id")// FK 컬럼명 = hospital_id
    private Facility facility;

    @Column(length = 100)
    private String department;// 진료과목

    @Column(length = 50)
    private String institutionType;// 의료기관

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean hasEmergency;// 응급실 여부 (기본 0=false)
}
