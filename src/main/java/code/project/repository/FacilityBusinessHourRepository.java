package code.project.repository;

import code.project.domain.FacilityBusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FacilityBusinessHourRepository extends JpaRepository<FacilityBusinessHour, Long> {

    List<FacilityBusinessHour> findByFacility_FacilityIdOrderByIdAsc(Long facilityId);

    List<FacilityBusinessHour> findByFacility_FacilityIdInOrderByIdAsc(Collection<Long> facilityIds);
}
