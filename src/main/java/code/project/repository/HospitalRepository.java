package code.project.repository;

import code.project.domain.Hospital;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    // 병원 검색 (거리 포함, 네이티브 쿼리)
    @Query(
            value = """
            SELECT 
              h.hospital_id AS hospital_id,
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
              AND (:emergency IS NULL OR h.has_emergency   = :emergency)
            ORDER BY distance ASC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM hospital h
            JOIN facility f ON f.facility_id = h.facility_id
            WHERE (:keyword   IS NULL OR h.hospital_name   LIKE CONCAT('%', :keyword, '%'))
              AND (:dept      IS NULL OR h.departments_csv LIKE CONCAT('%', :dept, '%'))
              AND (:org       IS NULL OR h.org_type        LIKE CONCAT('%', :org, '%'))
              AND (:emergency IS NULL OR h.has_emergency   = :emergency)
        """,
            nativeQuery = true
    )
    Page<Object[]> searchHospitalsWithDistanceNative(
            @Param("keyword") String keyword,
            @Param("dept") String dept,
            @Param("org") String org,
            @Param("emergency") Boolean emergency,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            Pageable pageable
    );

    // 배치로 엔티티 로드
    @EntityGraph(attributePaths = { "facility", "facility.businessHours" })
    List<Hospital> findByHospitalIdIn(List<Long> ids);
}
