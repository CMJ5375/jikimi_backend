package code.project.repository;

import code.project.domain.HospitalInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalInstitutionRepository extends JpaRepository<HospitalInstitution, Long> {
    // 특정 병원의 의료자원 목록 조회
    List<HospitalInstitution> findByHospital_HospitalId(Long hospitalId);
}
