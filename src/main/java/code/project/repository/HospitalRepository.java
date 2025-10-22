package code.project.repository;

import code.project.domain.Hospital;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    // 목록 조회 시 facility 및 그 하위 businessHours까지 미리 로딩
    @EntityGraph(attributePaths = {"facility", "facility.businessHours"})
    Page<Hospital> findAll(Pageable pageable);

    // 상세 조회 시 department, institution, facility.businessHours까지 한 번에
    @EntityGraph(attributePaths = {
            "facility", "facility.businessHours",
            "departments", "institutions"
    })
    Optional<Hospital> findById(Long id);
}
