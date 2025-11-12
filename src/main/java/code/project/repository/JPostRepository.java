package code.project.repository;

import code.project.domain.BoardCategory;
import code.project.domain.JPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JPostRepository extends JpaRepository<JPost, Long> {

    // --- 기존 사용 메서드들 (변경 없음) ---
    List<JPost> findByUser_UserId(Long userId);
    Page<JPost> findByIsDeletedFalse(Pageable pageable);
    Page<JPost> findByBoardCategoryAndIsDeletedFalse(BoardCategory boardCategory, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update JPost p set p.viewCount = p.viewCount + 1 where p.postId = :id")
    int incrementView(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update JPost p set p.likeCount = p.likeCount + 1 where p.postId = :id")
    int incrementLike(@Param("id") Long id);

    // ✅ CLOB 안전: cast(...) → concat('', p.content)
    @Query("""
           select p from JPost p
           where p.isDeleted = false
             and (
                  :q is null
                  or p.title like concat('%', :q, '%')
                  or concat('', p.content) like concat('%', :q, '%')
                 )
           order by p.postId desc
           """)
    Page<JPost> searchAll(@Param("q") String q, Pageable pageable);

    // ✅ CLOB 안전 + 정렬 명시
    @Query("""
           select p from JPost p
           where p.isDeleted = false
             and p.boardCategory = :boardCategory
             and (
                  :q is null
                  or p.title like concat('%', :q, '%')
                  or concat('', p.content) like concat('%', :q, '%')
                 )
           order by p.postId desc
           """)
    Page<JPost> searchByBoard(@Param("boardCategory") BoardCategory boardCategory,
                              @Param("q") String q,
                              Pageable pageable);

    List<JPost> findByUser_UsernameAndIsDeletedFalseOrderByPostIdDesc(String username);

    // ✅ 기본 목록(정렬 명시 + CLOB 안전)
    @Query("""
           select p from JPost p
           where p.isDeleted = false
             and ( :category is null or p.boardCategory = :category )
             and (
                  :q is null
                  or p.title like concat('%', :q, '%')
                  or concat('', p.content) like concat('%', :q, '%')
                 )
           order by p.postId desc
           """)
    Page<JPost> findDefault(@Param("category") BoardCategory category,
                            @Param("q") String q,
                            Pageable pageable);

    // ✅ 인기글(정렬 명시 + CLOB 안전)
    @Query("""
           select p from JPost p
           where p.isDeleted = false
             and (p.createdAt is null or p.createdAt >= :since)
             and p.likeCount >= 3
             and (:category is null or p.boardCategory = :category)
             and (
                  :q is null
                  or p.title like concat('%', :q, '%')
                  or concat('', p.content) like concat('%', :q, '%')
                 )
           order by p.likeCount desc, p.postId desc
           """)
    Page<JPost> findPopular(@Param("category") BoardCategory category,
                            @Param("q") String q,
                            @Param("since") LocalDateTime since,
                            Pageable pageable);

    // ✅ 핫핀용(Top N). 서비스에서 threshold=3 사용
    List<JPost> findTop3ByLikeCountGreaterThanEqualAndIsDeletedFalseOrderByLikeCountDescPostIdDesc(int threshold);

    // ✅ 단건 상세 user 조인 (기존처럼 유지)
    @EntityGraph(attributePaths = "user")
    Optional<JPost> findById(Long id);
}
