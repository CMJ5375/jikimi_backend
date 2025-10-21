package code.project.repository;

import code.project.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 작성자별 게시글 조회
    List<Post> findByUser_UserId(Long userId);

    // 삭제되지 않은 게시글만 조회
    List<Post> findByIsDeletedFalse();
}
