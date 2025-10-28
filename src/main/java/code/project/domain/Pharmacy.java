package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pharmacy")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pharmacy_id")
    private Long pharmacyId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false, unique = true)
    private Facility facility;

    @Column(nullable = false, length = 255)
    private String pharmacyName;

}
