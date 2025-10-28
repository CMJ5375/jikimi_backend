package code.project.repository;

import code.project.domain.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    // 거리 + 키워드 검색 (수정됨)
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
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable
    );

    // 목록 조회 시 facility + businessHours 함께 조회
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    Page<Pharmacy> findAll(Pageable pageable);

    // 상세 조회 시 facility + businessHours 함께 조회
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    Pharmacy findByPharmacyId(Long pharmacyId);
}
