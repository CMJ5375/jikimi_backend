package code.project.repository;

import code.project.domain.HospitalBusinessHour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalBusinessHourRepository extends JpaRepository<HospitalBusinessHour, Long> {
    // 특정 병원의 모든 진료시간 조회
    List<HospitalBusinessHour> findByHospital_HospitalId(Long hospitalId);
}
