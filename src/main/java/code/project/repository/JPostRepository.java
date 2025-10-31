package code.project.repository;

import code.project.domain.BoardCategory;
import code.project.domain.JPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JPostRepository extends JpaRepository<JPost, Long> {

    // --- 기존 사용 메서드들 ---
    List<JPost> findByUser_UserId(Long userId);

    Page<JPost> findByIsDeletedFalse(Pageable pageable);

    Page<JPost> findByBoardCategoryAndIsDeletedFalse(BoardCategory boardCategory, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update JPost p set p.viewCount = p.viewCount + 1 where p.postId = :id")
    int incrementView(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update JPost p set p.likeCount = p.likeCount + 1 where p.postId = :id")
    int incrementLike(@Param("id") Long id);

    // 검색(대소문자 변환/LOWER 제거, content는 문자열로 캐스팅)
    @Query("""
           select p from JPost p
           where p.isDeleted = false
             and (
                  :q is null
                  or p.title like concat('%', :q, '%')
                  or cast(p.content as string) like concat('%', :q, '%')
                 )
           """)
    Page<JPost> searchAll(@Param("q") String q, Pageable pageable);

    @Query("""
           select p from JPost p
           where p.isDeleted = false
             and p.boardCategory = :boardCategory
             and (
                  :q is null
                  or p.title like concat('%', :q, '%')
                  or cast(p.content as string) like concat('%', :q, '%')
                 )
           """)
    Page<JPost> searchByBoard(@Param("boardCategory") BoardCategory boardCategory,
                              @Param("q") String q,
                              Pageable pageable);

    List<JPost> findByUser_UsernameAndIsDeletedFalseOrderByPostIdDesc(String username);

    // 기본 목록(카테고리/검색 반영, 최신순) - LOWER 제거 + CLOB 캐스트
    @Query("""
           select p from JPost p
           where p.isDeleted = false
             and ( :category is null or p.boardCategory = :category )
             and (
                  :q is null
                  or p.title like concat('%', :q, '%')
                  or cast(p.content as string) like concat('%', :q, '%')
                 )
           """)
    Page<JPost> findDefault(@Param("category") BoardCategory category,
                            @Param("q") String q,
                            Pageable pageable);

    // 인기글(최근 since 이후) - LOWER 제거 + CLOB 캐스트
    @Query("""
   select p from JPost p
   where p.isDeleted = false
     and (p.createdAt is null or p.createdAt >= :since)
     and p.likeCount >= 3
     and (:category is null or p.boardCategory = :category)
     and (
           :q is null
           or p.title like concat('%', :q, '%')
           or cast(p.content as string) like concat('%', :q, '%')
         )
   """)
    Page<JPost> findPopular(@Param("category") BoardCategory category,
                            @Param("q") String q,
                            @Param("since") LocalDateTime since,
                            Pageable pageable);
}