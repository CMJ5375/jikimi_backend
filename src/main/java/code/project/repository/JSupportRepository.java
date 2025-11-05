package code.project.repository;

import code.project.domain.JSupport;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JSupportRepository extends JpaRepository<JSupport, Long> {

    // type 기준 목록 조회
    @Query("""
        SELECT s FROM JSupport s 
        WHERE LOWER(s.type) = LOWER(:type)
        ORDER BY s.pinnedCopy DESC, s.createdAt DESC
    """)
    Page<JSupport> findByTypeOrderByPinned(String type, Pageable pageable);

    // type + keyword 검색
    @Query("""
        SELECT s FROM JSupport s
        WHERE LOWER(s.type) = LOWER(:type)
        AND (
             LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR s.content LIKE CONCAT('%', :keyword, '%')
        )
        ORDER BY s.pinnedCopy DESC, s.createdAt DESC
        """)
    Page<JSupport> searchByTypeAndKeyword(String type, String keyword, Pageable pageable);

    // 상단 고정된 항목 (공지/자료실 전용)
    List<JSupport> findTop5ByTypeAndPinnedCopyIsTrueOrderByCreatedAtAsc(String type);

    long countByTypeAndPinnedCopyTrue(String type);
}
