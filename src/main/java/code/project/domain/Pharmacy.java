package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Pharmacy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "facility")
public class Pharmacy {

    @Id
    private Long pharmacyId;// PK = FK(Facility.facilityId) 기관 ID

    // Facility와 1:1, PK 공유
    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "pharmacy_id") //FK 컬럼명 = pharmacy_id
    private Facility facility; //기관명


    // TEXT: RPA로 긁어온 영업정보를 그대로 저장
    @Column(nullable = false)
    private boolean hasEmergency; //영업 정보 yes 아니면 no니까 불린형
}
