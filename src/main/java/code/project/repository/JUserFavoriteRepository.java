package code.project.repository;

import code.project.domain.JUserFavorite;
import code.project.domain.JUserFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JUserFavoriteRepository extends JpaRepository<JUserFavorite, JUserFavoriteId> {


    List<JUserFavorite> findById_UserId(Long userId);

    List<JUserFavorite> findById_FacilityId(Long facilityId);

    boolean existsById_UserIdAndId_FacilityId(Long userId, Long facilityId);

    long deleteById_UserIdAndId_FacilityId(Long userId, Long facilityId);
}
