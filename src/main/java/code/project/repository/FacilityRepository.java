package code.project.repository;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.Optional;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    // type(HOSPITAL or PHARMACY)별 시설 목록을 페이징 조회
    @EntityGraph(attributePaths = {"businessHours"}) // 영업시간 같이 조회
    Page<Facility> findByType(FacilityType type, Pageable pageable);

    // type과 이름(keyword)을 기준으로 부분 검색 + 페이징
    @EntityGraph(attributePaths = {"businessHours"}) // 영업시간 같이 조회
    Page<Facility> findByNameContainingAndType(String keyword, FacilityType type, Pageable pageable);

    @EntityGraph(attributePaths = {"businessHours"})
    Optional<Facility> findById(Long id);
}
