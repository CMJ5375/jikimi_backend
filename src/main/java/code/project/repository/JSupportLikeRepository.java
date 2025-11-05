package code.project.repository;

import code.project.domain.JSupportLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JSupportLikeRepository extends JpaRepository<JSupportLike, Long> {

    Optional<JSupportLike> findBySupport_SupportIdAndUser_UserId(Long supportId, Long userId);

    long countBySupport_SupportId(Long supportId);

    boolean existsBySupport_SupportIdAndUser_UserId(Long supportId, Long userId);
}
