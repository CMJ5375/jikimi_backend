package code.project.repository;

import code.project.domain.JComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JCommentRepository extends JpaRepository<JComment, Long> {
    @Query("""
           select c.post.postId as postId, count(c) as cnt
             from JComment c
            where c.post.postId in :postIds
            group by c.post.postId
           """)
    List<Object[]> countGroupByPostIds(Collection<Long> postIds);
    // 정렬은 Pageable에서 제어(DESC)
    @EntityGraph(attributePaths = "user")
    Page<JComment> findByPost_PostId(Long postId, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<JComment> findByUser_Username(String username, Pageable pageable);

    long countByPost_PostId(Long postId);

    // ⬇⬇⬇ 추가: 단건 조회도 user 같이
    @EntityGraph(attributePaths = "user")
    Optional<JComment> findById(Long id);


    // 소유자 확인용
    boolean existsByCommentIdAndUser_Username(Long commentId, String username);
}
