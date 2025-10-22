package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "facility")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id")
    private Long facilityId;

    // 기관 이름
    @Column(nullable = false, length = 255)
    private String name;

    // 병원 or 약국
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FacilityType type;

    // 대표 전화
    @Column(length = 20)
    private String phone;

    // 주소
    @Column(nullable = false, length = 255)
    private String address;

    // 위도 (DECIMAL(10,7))
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    // 경도 (DECIMAL(10,7))
    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    //지역 코드
    @Column(length = 20)
    private String regionCode;

    // 기관 구분
    @Column(length = 50)
    private String orgType;
}
