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
    private Long pharmacyId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(nullable = false, length = 255)
    private String pharmacyName;

}
