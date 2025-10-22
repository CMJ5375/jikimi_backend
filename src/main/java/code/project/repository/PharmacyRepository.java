package code.project.repository;

import code.project.domain.Pharmacy;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    // 목록 조회 시 facility + businessHours 함께 조회
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    Page<Pharmacy> findAll(Pageable pageable);

    // 상세 조회 시 facility + businessHours 함께 조회
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    Optional<Pharmacy> findById(Long id);
}
