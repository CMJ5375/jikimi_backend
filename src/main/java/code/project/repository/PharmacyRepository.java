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

    // 약국 검색 (거리 + 키워드)
    @Query("""
        SELECT p FROM Pharmacy p
        JOIN p.facility f
        WHERE (:keyword IS NULL OR :keyword = '' OR p.pharmacyName LIKE CONCAT('%', :keyword, '%'))
          AND (
            :radiusKm IS NULL OR
            (6371 * acos(
              cos(radians(:lat)) * cos(radians(f.latitude)) *
              cos(radians(f.longitude) - radians(:lng)) +
              sin(radians(:lat)) * sin(radians(f.latitude))
            )) <= :radiusKm
          )
        ORDER BY
          (6371 * acos(
            cos(radians(:lat)) * cos(radians(f.latitude)) *
            cos(radians(f.longitude) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(f.latitude))
          )) ASC
    """)
    Page<Pharmacy> searchPharmacies(
            @Param("keyword") String keyword,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable
    );

    // 즐겨찾기 전용 검색 username기준으로 Pharmacy 조인
    @Query("""
        SELECT p FROM JUserFavorite f
        JOIN f.pharmacy p
        JOIN p.facility ff
        WHERE f.user.username = :username
          AND (:keyword IS NULL OR p.pharmacyName LIKE %:keyword%)
        ORDER BY p.pharmacyId DESC
    """)
    Page<Pharmacy> searchFavoritePharmacies(
            @Param("username") String username,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 목록 조회 시 facility + businessHours 함께 조회
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    @Query("SELECT p FROM Pharmacy p")
    Page<Pharmacy> findAllWithFacility(Pageable pageable);

    // 상세 조회 시 facility + businessHours 함께 조회
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    @Query("SELECT p FROM Pharmacy p WHERE p.pharmacyId = :id")
    Optional<Pharmacy> findByIdWithFacility(@Param("id") Long id);
}
