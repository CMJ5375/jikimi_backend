package code.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "Facility",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "address"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Facility {
    //커밋 테스트용
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId; // PK

    @Column(nullable = false, length = 100)
    private String name; // 기관명

    // 'HOSPITAL' 또는 'PHARMACY'
    @Column(nullable = false, length = 20)
    private String type; // 유형

    @Column(length = 20)
    private String phone; // 전화번호

    @Column(nullable = false, length = 255)
    private String address; // 주소

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 20)
    private String regionCode; // 지역코드
}
