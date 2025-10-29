package code.project.repository;

import code.project.domain.JComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface JCommentRepository extends JpaRepository<JComment, Long> {
    @Query("""
           select c.post.postId as postId, count(c) as cnt
             from JComment c
            where c.post.postId in :postIds
            group by c.post.postId
           """)
    List<Object[]> countGroupByPostIds(Collection<Long> postIds);
    // 정렬은 Pageable에서 제어(DESC)
    Page<JComment> findByPost_PostId(Long postId, Pageable pageable);

    Page<JComment> findByUser_Username(String username, Pageable pageable);

    long countByPost_PostId(Long postId);

    // 소유자 확인용
    boolean existsByCommentIdAndUser_Username(Long commentId, String username);

}
