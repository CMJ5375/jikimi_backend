package code.project.repository;

import code.project.domain.JComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JCommentRepository extends JpaRepository<JComment, Long> {
    List<JComment> findByPost_PostId(Long postId);
}
