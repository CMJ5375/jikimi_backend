package code.project.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "j_user_favorite",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_fav_user_hospital", columnNames = {"user_id", "hospital_id"}),
                @UniqueConstraint(name = "uk_fav_user_pharmacy", columnNames = {"user_id", "pharmacy_id"})
        },
        indexes = {
                @Index(name = "idx_fav_user", columnList = "user_id"),
                @Index(name = "idx_fav_hospital", columnList = "hospital_id"),
                @Index(name = "idx_fav_pharmacy", columnList = "pharmacy_id")
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

    // 유저
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fav_user"))
    private JUser user;

    // 병원/약국 (둘 중 하나만 설정)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", foreignKey = @ForeignKey(name = "fk_fav_hospital"))
    private Hospital hospital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", foreignKey = @ForeignKey(name = "fk_fav_pharmacy"))
    private Pharmacy pharmacy;

    // ✅ 예약어 충돌 방지: 백틱으로 정확히 "type" 컬럼에 매핑
    @Enumerated(EnumType.STRING)
    @Column(name = "`type`", length = 20, nullable = false)
    private FacilityType type; // HOSPITAL | PHARMACY

    // ✅ 자동 생성일
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

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
