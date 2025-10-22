package code.project.repository;

import code.project.domain.Pharmacy;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    // 약국 검색 쿼리 = 거리순 + 전체 조회
    @Query("""
        SELECT p,
               (6371 * acos(
                   cos(radians(:lat)) * cos(radians(f.latitude))
                 * cos(radians(f.longitude) - radians(:lng))
                 + sin(radians(:lat)) * sin(radians(f.latitude))
               )) AS distance
        FROM Pharmacy p
        JOIN p.facility f
        WHERE (:keyword IS NULL OR p.pharmacyName LIKE CONCAT('%', :keyword, '%'))
        ORDER BY distance ASC
    """)
    Page<Object[]> searchPharmaciesWithDistance(@Param("keyword") String keyword,
                                                @Param("lat") double lat,
                                                @Param("lng") double lng,
                                                Pageable pageable);

    // 목록 조회 시 facility + businessHours 함께 조회
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    Page<Pharmacy> findAll(Pageable pageable);

    // 상세 조회 시 facility + businessHours 함께 조회
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    Optional<Pharmacy> findById(Long id);
}
