package code.project.repository;

import code.project.domain.JComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JCommentRepository extends JpaRepository<JComment, Long> {

    // 정렬은 Pageable에서 제어(DESC)
    Page<JComment> findByPost_PostId(Long postId, Pageable pageable);

    long countByPost_PostId(Long postId);

    // 소유자 확인용
    boolean existsByCommentIdAndUser_Username(Long commentId, String username);
}
