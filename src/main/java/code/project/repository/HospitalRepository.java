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

    /**
     * 병원 검색 (거리순 + 키워드 + 과목CSV + 기관유형 + 응급실)
     * - 네이티브 쿼리: MySQL 함수(ACOS/COS/SIN/RADIANS) 사용
     * - 리턴: Object[] => [Hospital 엔티티, distance(Double)]
     */
    @Query(
            value = """
        SELECT 
            h.*,
            (6371 * ACOS(
               COS(RADIANS(:lat)) * COS(RADIANS(f.latitude))
             * COS(RADIANS(f.longitude) - RADIANS(:lng))
             + SIN(RADIANS(:lat)) * SIN(RADIANS(f.latitude))
            )) AS distance
        FROM hospital h
        JOIN facility f ON f.facility_id = h.facility_id
        WHERE (:keyword   IS NULL OR h.hospital_name   LIKE CONCAT('%', :keyword, '%'))
          AND (:dept      IS NULL OR h.departments_csv LIKE CONCAT('%', :dept, '%'))
          AND (:org       IS NULL OR h.org_type        LIKE CONCAT('%', :org, '%'))
          AND (:emergency IS NULL OR h.has_emergency = :emergency)
        ORDER BY distance ASC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM hospital h
        JOIN facility f ON f.facility_id = h.facility_id
        WHERE (:keyword   IS NULL OR h.hospital_name   LIKE CONCAT('%', :keyword, '%'))
          AND (:dept      IS NULL OR h.departments_csv LIKE CONCAT('%', :dept, '%'))
          AND (:org       IS NULL OR f.org_type        LIKE CONCAT('%', :org, '%'))
          AND (:emergency IS NULL OR h.has_emergency = :emergency)
        """,
            nativeQuery = true
    )
    Page<Object[]> searchHospitalsWithDistanceNative(
            @Param("keyword") String keyword,
            @Param("org") String org,
            @Param("dept") String dept,
            @Param("emergency") Boolean emergency,
            @Param("lat") double lat,
            @Param("lng") double lng,
            Pageable pageable
    );

    /**
     * 목록 조회: facility + businessHours 프리패치
     */
    @EntityGraph(attributePaths = { "facility", "facility.businessHours" })
    Page<Hospital> findAll(Pageable pageable);

    /**
     * 상세 조회: facility + businessHours 프리패치
     */
    @EntityGraph(attributePaths = { "facility", "facility.businessHours" })
    Optional<Hospital> findById(Long id);
}
