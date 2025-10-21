package code.project.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(
        name = "facility",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "address"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "departments")
public class Facility {
    //커밋 테스트용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId; // PK

    @Column(nullable = false, length = 100)
    private String name; // 기관명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FacilityType type; // 'HOSPITAL' 또는 'PHARMACY'

    @Column(length = 20)
    private String phone; // 전화번호

    @Column(nullable = false, length = 255)
    private String address; // 주소

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude; // 위도

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude; // 경도

    @Column(length = 20)
    private String regionCode; // 지역코드

    @Column(length = 50)
    private String orgType; // 의료기관

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<FacilityDepartment> departments; // 진료과목
}
