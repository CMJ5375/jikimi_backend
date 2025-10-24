package code.project.repository;

import code.project.domain.JPost;
import code.project.domain.BoardCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JPostRepository extends JpaRepository<JPost, Long> {

    List<JPost> findByUser_UserId(Long userId);

    Page<JPost> findByIsDeletedFalse(Pageable pageable);

    // 타입 바꾸기
    Page<JPost> findByBoardCategoryAndIsDeletedFalse(BoardCategory boardCategory, Pageable pageable);

    //조회수
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update JPost p set p.viewCount = p.viewCount + 1 where p.postId = :id")
    int incrementView(@Param("id") Long id);

    @Query("""
           select p from JPost p
           where p.isDeleted = false
             and (p.title like concat('%', :q, '%')
              or p.content like concat('%', :q, '%'))
           """)
    Page<JPost> searchAll(@Param("q") String q, Pageable pageable);

    @Query("""
           select p from JPost p
           where p.isDeleted = false
             and p.boardCategory = :boardCategory
             and (p.title like concat('%', :q, '%')
              or p.content like concat('%', :q, '%'))
           """)
    Page<JPost> searchByBoard(@Param("boardCategory") BoardCategory boardCategory, // 👈 타입 변경
                              @Param("q") String q,
                              Pageable pageable);
}