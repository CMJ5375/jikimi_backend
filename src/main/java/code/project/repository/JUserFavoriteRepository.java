package code.project.repository;

import code.project.domain.JUserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JUserFavoriteRepository extends JpaRepository<JUserFavorite, Long> {

    List<JUserFavorite> findById_UserId(Long userId);

    boolean existsById_UserIdAndId_FacilityId(Long userId, Long facilityId);

    void deleteById_UserIdAndId_FacilityId(Long userId, Long facilityId);

    @Query("SELECT uf.id.facilityId FROM JUserFavorite uf WHERE uf.id.userId = :userId")
    List<Long> findFacilityIdsByUserId(Long userId);
}