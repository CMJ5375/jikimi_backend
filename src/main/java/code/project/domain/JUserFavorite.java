package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "User_Favorite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "facility"})
public class JUserFavorite {

    @EmbeddedId
    private JUserFavoriteId id; // (user_id, facility_id) 복합 PK

    // user_id를 PK의 일부로 공유
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId") // EmbeddedId의 필드명과 일치해야 함
    @JoinColumn(name = "user_id", nullable = false)
    private JUser user;

    // facility_id를 PK의 일부로 공유
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("facilityId") // EmbeddedId의 필드명과 일치해야 함
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    // 팩토리 메서드 (매번 userid와 기관id 가져오기 귀찮으니 묶어버림)
    public static JUserFavorite of(JUser user, Facility facility) {
        JUserFavorite uf = new JUserFavorite();
        uf.setUser(user);
        uf.setFacility(facility);
        uf.setId(new JUserFavoriteId(user.getUserId(), facility.getFacilityId()));
        return uf;
    }
}
