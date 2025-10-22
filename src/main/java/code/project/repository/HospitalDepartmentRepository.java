package code.project.repository;

import code.project.domain.HospitalDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalDepartmentRepository extends JpaRepository<HospitalDepartment, Long> {
    // 특정 병원의 모든 진료과목 조회
    List<HospitalDepartment> findByHospital_HospitalId(Long hospitalId);
}
