package code.project.repository;

import code.project.domain.FacilityDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FacilityDepartmentRepository extends JpaRepository<FacilityDepartment, Long> {
    List<FacilityDepartment> findByFacility_FacilityId(Long facilityId);
}
