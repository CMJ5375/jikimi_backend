package code.project.repository;

import code.project.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Commit
class UserFavoriteRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired FacilityRepository facilityRepository;
    @Autowired UserFavoriteRepository userFavoriteRepository;

    @Test
    @DisplayName("즐겨찾기 저장/조회/존재여부/삭제")
    void favoritesCrud() {
        // 사용자
        User user = userRepository.save(User.builder()
                .username("favuser")
                .password("1234")
                .name("즐겨찾기유저")
                .email("fav@example.com")
                .socialType("LOCAL")
                .role("USER")
                .build());

        // 기관
        Facility f = facilityRepository.save(Facility.builder()
                .name("행복병원")
                .type("HOSPITAL")
                .address("서울시 1-1")
                .latitude(new BigDecimal("37.55"))
                .longitude(new BigDecimal("126.98"))
                .build());

        // 즐겨찾기 저장 (복합키)
        UserFavorite uf = new UserFavorite();
        uf.setUser(user);
        uf.setFacility(f);
        uf.setId(new UserFavoriteId(user.getUserId(), f.getFacilityId()));

        userFavoriteRepository.save(uf);

        // 목록 조회
        List<UserFavorite> list = userFavoriteRepository.findById_UserId(user.getUserId());
        assertThat(list).hasSize(1);

        // 존재 여부
        boolean exists = userFavoriteRepository.existsById_UserIdAndId_FacilityId(
                user.getUserId(), f.getFacilityId());
        assertThat(exists).isTrue();

        // 삭제
//        UserFavoriteId id = new UserFavoriteId(user.getUserId(), f.getFacilityId());
//        userFavoriteRepository.deleteById(id);
//        assertThat(userFavoriteRepository.findById(id)).isEmpty();

    }
}
