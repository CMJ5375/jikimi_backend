package code.project.service;

import code.project.domain.*;
import code.project.dto.HospitalDTO;
import code.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalDepartmentRepository departmentRepository;
    private final HospitalBusinessHourRepository businessHourRepository;
    private final HospitalInstitutionRepository institutionRepository;

    // 병원 목록 조회 (페이징)
    // -> Hospital 엔티티를 HospitalDTO로 변환
    public Page<HospitalDTO> getHospitalList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("hospitalName").ascending());
        return hospitalRepository.findAll(pageable)
                .map(HospitalDTO::fromEntity);
    }

    // 병원 상세 조회 (연관 엔티티 포함)
    public HospitalDTO getHospitalDetail(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        return HospitalDTO.fromEntity(hospital);
    }

    // 병원 진료과목 목록 조회
    public List<HospitalDepartment> getDepartments(Long hospitalId) {
        return departmentRepository.findByHospital_HospitalId(hospitalId);
    }

    // 병원 요일별 진료시간 조회
    public List<HospitalBusinessHour> getBusinessHours(Long hospitalId) {
        return businessHourRepository.findByHospital_HospitalId(hospitalId);
    }

    // 병원 의료자원(장비 등) 목록 조회
    public List<HospitalInstitution> getInstitutions(Long hospitalId) {
        return institutionRepository.findByHospital_HospitalId(hospitalId);
    }
}
