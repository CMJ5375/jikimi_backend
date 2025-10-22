package code.project.repository;

import code.project.domain.Hospital;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    // 병원 검색 쿼리 = 거리순 + 응급실 여부 필터 + 전체 조회
    @Query("""
        SELECT DISTINCT h,
               (6371 * acos(
                   cos(radians(:lat)) * cos(radians(f.latitude))
                 * cos(radians(f.longitude) - radians(:lng))
                 + sin(radians(:lat)) * sin(radians(f.latitude))
               )) AS distance
        FROM Hospital h
        JOIN h.facility f
        LEFT JOIN h.departments d
        WHERE (:keyword IS NULL OR h.hospitalName LIKE CONCAT('%', :keyword, '%'))
          AND (:org IS NULL OR f.orgType LIKE CONCAT('%', :org, '%'))
          AND (:dept IS NULL OR d.departmentName LIKE CONCAT('%', :dept, '%'))
          AND (:emergency IS NULL OR h.hasEmergency = :emergency)
        ORDER BY distance ASC
    """)
    Page<Object[]> searchHospitalsWithDistance(@Param("keyword") String keyword,
                                               @Param("org") String org,
                                               @Param("dept") String dept,
                                               @Param("emergency") Boolean emergency,
                                               @Param("lat") double lat,
                                               @Param("lng") double lng,
                                               Pageable pageable);

    // 목록 조회 시 facility 및 그 하위 businessHours까지 미리 로딩
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    Page<Hospital> findAll(Pageable pageable);

    // 상세 조회 시 department, institution, facility.businessHours까지 한 번에
    @EntityGraph(attributePaths = {
            "facility", "facility.businessHours",
            "departments", "institutions"
    })
    Optional<Hospital> findById(Long id);
}
