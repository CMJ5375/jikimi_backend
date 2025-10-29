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
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    @Query("""
        SELECT h FROM Hospital h
        JOIN FETCH h.facility f
        WHERE (:keyword IS NULL OR :keyword = '' OR LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:dept IS NULL OR :dept = '' OR
            CONCAT(',', REPLACE(COALESCE(h.departmentsCsv, ''), ' ', ''), ',')
            LIKE CONCAT('%,', REPLACE(:dept, ' ', ''), ',%'))
        AND (:org IS NULL OR :org = '' OR LOWER(TRIM(h.orgType)) = LOWER(TRIM(:org)))
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
    @EntityGraph(attributePaths = { "facility", "facility.businessHours" })
    @Query("""
        SELECT h FROM JUserFavorite f
        JOIN f.hospital h
        JOIN h.facility ff
        WHERE f.user.username = :username
        AND (:keyword IS NULL OR LOWER(ff.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:dept IS NULL OR :dept = '' OR
            CONCAT(',', REPLACE(COALESCE(h.departmentsCsv, ''), ' ', ''), ',')
            LIKE CONCAT('%,', REPLACE(:dept, ' ', ''), ',%'))
        AND (:org IS NULL OR :org = '' OR LOWER(TRIM(h.orgType)) = LOWER(TRIM(:org)))
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

    // 병원 목록 페이징
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    @Query("SELECT h FROM Hospital h")
    Page<Hospital> findAllWithFacility(Pageable pageable);

    // 병원 상세
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    @Query("SELECT h FROM Hospital h WHERE h.hospitalId = :id")
    Optional<Hospital> findByIdWithFacility(@Param("id") Long id);
}
