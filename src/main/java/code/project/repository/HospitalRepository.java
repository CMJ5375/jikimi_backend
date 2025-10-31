package code.project.repository;

import code.project.domain.Hospital;
import code.project.service.HospitalListView; // ← 너가 만든 프로젝션 인터페이스 경로
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    // ==========================
    // 기존 엔티티 반환 메서드들 (유지)
    // ==========================

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


    // ==========================
    // 신규: 경량 목록용 "프로젝션" 메서드들
    // ==========================

    /**
     * 경량 목록 조회 (엔티티 대신 프로젝션 반환)
     * - 필요한 필드만 select → 페이로드/영속성 부담 ↓
     * - 기존 searchHospitals와 동일한 필터 조건/거리정렬 재사용
     */
    @Query("""
        select
          h.hospitalId   as hospitalId,
          h.hospitalName as hospitalName,
          h.hasEmergency as hasEmergency,
          f.latitude     as latitude,
          f.longitude    as longitude,
          f.address      as address
        from Hospital h
          join h.facility f
        where (:keyword IS NULL OR :keyword = '' OR LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          and (:dept IS NULL OR :dept = '' OR
               CONCAT(',', REPLACE(COALESCE(h.departmentsCsv, ''), ' ', ''), ',')
               LIKE CONCAT('%,', REPLACE(:dept, ' ', ''), ',%'))
          and (:org IS NULL OR :org = '' OR LOWER(TRIM(h.orgType)) = LOWER(TRIM(:org)))
          and (:emergency IS NULL OR h.hasEmergency = :emergency)
        order by
          (6371 * acos(
              cos(radians(:lat)) * cos(radians(f.latitude)) *
              cos(radians(f.longitude) - radians(:lng)) +
              sin(radians(:lat)) * sin(radians(f.latitude))
          )) ASC
        """)
    Page<HospitalListView> findListLite(
            @Param("keyword") String keyword,
            @Param("dept") String dept,
            @Param("org") String org,
            @Param("emergency") Boolean emergency,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            Pageable pageable
    );

    /**
     * 즐겨찾기 경량 목록 (username 기준, 프로젝션 반환)
     * - 기존 searchFavoriteHospitals와 동일한 필터/정렬
     */
    @Query("""
        select
          h.hospitalId   as hospitalId,
          h.hospitalName as hospitalName,
          h.hasEmergency as hasEmergency,
          f.latitude     as latitude,
          f.longitude    as longitude,
          f.address      as address
        from JUserFavorite uf
          join uf.hospital h
          join h.facility f
        where uf.user.username = :username
          and (:keyword IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          and (:dept IS NULL OR :dept = '' OR
               CONCAT(',', REPLACE(COALESCE(h.departmentsCsv, ''), ' ', ''), ',')
               LIKE CONCAT('%,', REPLACE(:dept, ' ', ''), ',%'))
          and (:org IS NULL OR :org = '' OR LOWER(TRIM(h.orgType)) = LOWER(TRIM(:org)))
          and (:emergency IS NULL OR h.hasEmergency = :emergency)
        order by h.hospitalId desc
        """)
    Page<HospitalListView> findFavoriteListLite(
            @Param("username") String username,
            @Param("keyword") String keyword,
            @Param("dept") String dept,
            @Param("org") String org,
            @Param("emergency") Boolean emergency,
            Pageable pageable
    );
}
