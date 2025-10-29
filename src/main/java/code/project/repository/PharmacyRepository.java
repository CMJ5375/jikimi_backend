package code.project.repository;

import code.project.domain.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    /** 일반 검색 — 반경 필터 O (네이티브) */
    @Query(
            value = """
            SELECT p.*
            FROM pharmacy p
            JOIN facility f ON f.facility_id = p.facility_id
            WHERE (:keyword IS NULL OR :keyword = '' OR p.pharmacy_name LIKE CONCAT('%', :keyword, '%'))
              AND :lat IS NOT NULL AND :lng IS NOT NULL AND :radiusKm IS NOT NULL
              AND (
                6371 * ACOS(
                  COS(RADIANS(:lat)) * COS(RADIANS(f.latitude)) *
                  COS(RADIANS(f.longitude) - RADIANS(:lng)) +
                  SIN(RADIANS(:lat)) * SIN(RADIANS(f.latitude))
                )
              ) <= :radiusKm
            ORDER BY
              6371 * ACOS(
                COS(RADIANS(:lat)) * COS(RADIANS(f.latitude)) *
                COS(RADIANS(f.longitude) - RADIANS(:lng)) +
                SIN(RADIANS(:lat)) * SIN(RADIANS(f.latitude))
              ) ASC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM pharmacy p
            JOIN facility f ON f.facility_id = p.facility_id
            WHERE (:keyword IS NULL OR :keyword = '' OR p.pharmacy_name LIKE CONCAT('%', :keyword, '%'))
              AND :lat IS NOT NULL AND :lng IS NOT NULL AND :radiusKm IS NOT NULL
              AND (
                6371 * ACOS(
                  COS(RADIANS(:lat)) * COS(RADIANS(f.latitude)) *
                  COS(RADIANS(f.longitude) - RADIANS(:lng)) +
                  SIN(RADIANS(:lat)) * SIN(RADIANS(f.latitude))
                )
              ) <= :radiusKm
        """,
            nativeQuery = true
    )
    Page<Pharmacy> searchPharmaciesWithin(
            @Param("keyword") String keyword,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable
    );

    /** 일반 검색 — 반경 필터 X (네이티브, 정렬은 이름/ID 등) */
    @Query(
            value = """
            SELECT p.*
            FROM pharmacy p
            WHERE (:keyword IS NULL OR :keyword = '' OR p.pharmacy_name LIKE CONCAT('%', :keyword, '%'))
            ORDER BY p.pharmacy_id ASC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM pharmacy p
            WHERE (:keyword IS NULL OR :keyword = '' OR p.pharmacy_name LIKE CONCAT('%', :keyword, '%'))
        """,
            nativeQuery = true
    )
    Page<Pharmacy> searchPharmaciesAll(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    /** 즐겨찾기 — 반경 필터 O (네이티브) */
    @Query(
            value = """
            SELECT p.*
            FROM pharmacy p
            JOIN facility f ON f.facility_id = p.facility_id
            JOIN user_favorite uf ON uf.pharmacy_id = p.pharmacy_id
            JOIN `User` u ON u.user_id = uf.user_id
            WHERE u.username = :username
              AND (:keyword IS NULL OR :keyword = '' OR p.pharmacy_name LIKE CONCAT('%', :keyword, '%'))
              AND :lat IS NOT NULL AND :lng IS NOT NULL AND :radiusKm IS NOT NULL
              AND (
                6371 * ACOS(
                  COS(RADIANS(:lat)) * COS(RADIANS(f.latitude)) *
                  COS(RADIANS(f.longitude) - RADIANS(:lng)) +
                  SIN(RADIANS(:lat)) * SIN(RADIANS(f.latitude))
                )
              ) <= :radiusKm
            ORDER BY
              6371 * ACOS(
                COS(RADIANS(:lat)) * COS(RADIANS(f.latitude)) *
                COS(RADIANS(f.longitude) - RADIANS(:lng)) +
                SIN(RADIANS(:lat)) * SIN(RADIANS(f.latitude))
              ) ASC
        """,
            countQuery = """
            SELECT COUNT(*)
            FROM pharmacy p
            JOIN facility f ON f.facility_id = p.facility_id
            JOIN user_favorite uf ON uf.pharmacy_id = p.pharmacy_id
            JOIN `User` u ON u.user_id = uf.user_id
            WHERE u.username = :username
              AND (:keyword IS NULL OR :keyword = '' OR p.pharmacy_name LIKE CONCAT('%', :keyword, '%'))
              AND :lat IS NOT NULL AND :lng IS NOT NULL AND :radiusKm IS NOT NULL
              AND (
                6371 * ACOS(
                  COS(RADIANS(:lat)) * COS(RADIANS(f.latitude)) *
                  COS(RADIANS(f.longitude) - RADIANS(:lng)) +
                  SIN(RADIANS(:lat)) * SIN(RADIANS(f.latitude))
                )
              ) <= :radiusKm
        """,
            nativeQuery = true
    )
    Page<Pharmacy> searchFavoritePharmaciesWithin(
            @Param("username") String username,
            @Param("keyword") String keyword,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable
    );

    /** 즐겨찾기 — 반경 필터 X (JPQL) */
    @Query(""" 
        SELECT p FROM JUserFavorite uf
        JOIN uf.pharmacy p
        JOIN p.facility f
        WHERE uf.user.username = :username
          AND (:keyword IS NULL OR p.pharmacyName LIKE %:keyword%)
        ORDER BY p.pharmacyId DESC
    """)
    Page<Pharmacy> searchFavoritePharmaciesAll(
            @Param("username") String username,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    @Query("SELECT p FROM Pharmacy p")
    Page<Pharmacy> findAllWithFacility(Pageable pageable);

    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    @Query("SELECT p FROM Pharmacy p WHERE p.pharmacyId = :id")
    Optional<Pharmacy> findByIdWithFacility(@Param("id") Long id);
}
