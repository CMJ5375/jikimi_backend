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

    // 병원 검색 (거리 포함, JPQL)
    @Query("""
        SELECT h FROM Hospital h
        JOIN FETCH h.facility f
        WHERE (:keyword IS NULL OR :keyword = '' OR h.hospitalName LIKE CONCAT('%', :keyword, '%'))
          AND (:dept IS NULL OR h.departmentsCsv LIKE CONCAT('%', :dept, '%'))
          AND (:org IS NULL OR h.orgType = :org)
          AND (:emergency IS NULL OR h.hasEmergency = :emergency)
        ORDER BY
          (6371 * acos(
            cos(radians(:lat)) * cos(radians(f.latitude)) *
            cos(radians(f.longitude) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(f.latitude))
          )) ASC
    """)
    Page<Hospital> searchHospitals(
            @Param("keyword") String keyword,
            @Param("dept") String dept,
            @Param("org") String org,
            @Param("emergency") Boolean emergency,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            Pageable pageable
    );

    // 즐겨찾기 전용 검색 (username 기준)
    @Query("""
        SELECT h FROM JUserFavorite f
        JOIN f.hospital h
        JOIN h.facility ff
        WHERE f.user.username = :username
          AND (:keyword IS NULL OR h.hospitalName LIKE %:keyword%)
          AND (:dept IS NULL OR h.departmentsCsv LIKE %:dept%)
          AND (:org IS NULL OR h.orgType = :org)
          AND (:emergency IS NULL OR h.hasEmergency = :emergency)
        ORDER BY h.hospitalId DESC
    """)
    Page<Hospital> searchFavoriteHospitals(
            @Param("username") String username,
            @Param("keyword") String keyword,
            @Param("dept") String dept,
            @Param("org") String org,
            @Param("emergency") Boolean emergency,
            Pageable pageable
    );

    // 목록/상세 최적화
    @EntityGraph(attributePaths = { "facility", "facility.businessHours" })
    @Query("select h from Hospital h")
    Page<Hospital> findAllWithFacility(Pageable pageable);

    @EntityGraph(attributePaths = { "facility", "facility.businessHours" })
    @Query("select h from Hospital h where h.hospitalId = :id")
    Optional<Hospital> findByIdWithFacility(@Param("id") Long id);

    // 배치로 엔티티 로드 (필요 시 유지)
    @EntityGraph(attributePaths = { "facility", "facility.businessHours" })
    List<Hospital> findByHospitalIdIn(List<Long> ids);
}
