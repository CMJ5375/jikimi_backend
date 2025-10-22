package code.project.repository;

import code.project.domain.FacilityBusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityBusinessHourRepository extends JpaRepository<FacilityBusinessHour, Long> {

    // 특정 시설(Facility)의 모든 영업시간 조회
    List<FacilityBusinessHour> findByFacility_FacilityId(Long facilityId);

}
