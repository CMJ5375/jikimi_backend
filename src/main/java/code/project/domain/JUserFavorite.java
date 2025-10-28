package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_favorite",
        uniqueConstraints = {
                // 병원 즐겨찾기 중복 방지
                @UniqueConstraint(name = "uk_fav_user_hospital", columnNames = {"user_id", "hospital_id"}),
                // 약국 즐겨찾기 중복 방지
                @UniqueConstraint(name = "uk_fav_user_pharmacy", columnNames = {"user_id", "pharmacy_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "hospital", "pharmacy"})
public class JUserFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    //유저
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private JUser user;

    // 병원/약국을 분리 저장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;

    // 타입 힌트 (UI/조회 편의)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20, nullable = false)
    private FacilityType type; // HOSPITAL or PHARMACY

    // ===== 편의 팩토리 =====
    public static JUserFavorite ofHospital(JUser user, Hospital hospital) {
        return JUserFavorite.builder()
                .user(user)
                .hospital(hospital)
                .pharmacy(null)
                .type(FacilityType.HOSPITAL)
                .build();
    }

    public static JUserFavorite ofPharmacy(JUser user, Pharmacy pharmacy) {
        return JUserFavorite.builder()
                .user(user)
                .hospital(null)
                .pharmacy(pharmacy)
                .type(FacilityType.PHARMACY)
                .build();
    }
}
