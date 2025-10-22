package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hospital_department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    // 진료 과목
    @Column(nullable = false, length = 100)
    private String departmentName;
}
