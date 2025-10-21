package code.project.repository;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    // 기존
    List<Facility> findByType(FacilityType type);
    List<Facility> findByTypeAndNameContainingIgnoreCase(FacilityType type, String name);

    // 새 검색 메서드 (의료기관 + 진료과목 필터 포함)
    @Query("""
        SELECT DISTINCT f FROM Facility f
        LEFT JOIN f.departments d
        WHERE f.type = :type
        AND (:name IS NULL OR f.name LIKE %:name%)
        AND (:orgType IS NULL OR f.orgType = :orgType)
        AND (:deptName IS NULL OR d.departmentName = :deptName)
    """)
    List<Facility> searchFacilities(
            @Param("type") FacilityType type,
            @Param("name") String name,
            @Param("orgType") String orgType,
            @Param("deptName") String deptName
    );
}
