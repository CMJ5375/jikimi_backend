package code.project.repository;

import code.project.domain.JPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JPostRepository extends JpaRepository<JPost, Long> {

    // 작성자별 게시글 조회
    List<JPost> findByJUser_UserId(Long userId);

    // 삭제되지 않은 게시글만 조회
    List<JPost> findByIsDeletedFalse();
}
