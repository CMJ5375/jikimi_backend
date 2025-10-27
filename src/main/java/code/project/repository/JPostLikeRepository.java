package code.project.repository;

import code.project.domain.JPostLike;
import code.project.domain.JPost;
import code.project.domain.JUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JPostLikeRepository extends JpaRepository<JPostLike, Long> {

    // 이 유저가 이 글에 이미 좋아요 했는지 확인
    Optional<JPostLike> findByPostAndUser(JPost post, JUser user);

    // 좋아요 취소
    void deleteByPostAndUser(JPost post, JUser user);
}