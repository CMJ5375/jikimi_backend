package code.project.repository;

import code.project.domain.UserFavorite;
import code.project.domain.UserFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFavoriteRepository extends JpaRepository<UserFavorite, UserFavoriteId> {


    List<UserFavorite> findById_UserId(Long userId);

    List<UserFavorite> findById_FacilityId(Long facilityId);

    boolean existsById_UserIdAndId_FacilityId(Long userId, Long facilityId);

    long deleteById_UserIdAndId_FacilityId(Long userId, Long facilityId);
}
