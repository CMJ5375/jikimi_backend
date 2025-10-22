package code.project.service;

import code.project.domain.Hospital;
import code.project.domain.HospitalDepartment;
import code.project.domain.HospitalInstitution;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.dto.HospitalDTO;
import code.project.repository.HospitalDepartmentRepository;
import code.project.repository.HospitalInstitutionRepository;
import code.project.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalDepartmentRepository departmentRepository;
    private final HospitalInstitutionRepository institutionRepository;

    //병원 검색 서비스 로직
    public Page<HospitalDTO> searchHospitals(String keyword, String org, String dept,
                                             Boolean emergency, double lat, double lng, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (org != null && org.trim().isEmpty()) org = null;
        if (dept != null && dept.trim().isEmpty()) dept = null;

        Page<Object[]> result = hospitalRepository.searchHospitalsWithDistance(keyword, org, dept, emergency, lat, lng, pageable);

        List<HospitalDTO> dtoList = result.getContent().stream()
                .map(row -> {
                    Hospital hospital = (Hospital) row[0];
                    Double distance = (Double) row[1];
                    return HospitalDTO.fromEntity(hospital).withDistance(distance);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, result.getTotalElements());
    }

    /**
     * 병원 목록 조회 (페이징)
     * DTO 변환 시 facility 및 facility.businessHours 접근이 있으므로
     * 1) @Transactional(readOnly = true) 로 세션 보장
     * 2) 또는 HospitalRepository에서 @EntityGraph로 facility(+businessHours) 프리패치 권장
     */
    @Transactional(readOnly = true)
    public Page<HospitalDTO> getHospitalList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("hospitalName").ascending());
        return hospitalRepository.findAll(pageable)
                .map(HospitalDTO::fromEntity);
    }

    /**
     * 병원 상세 조회
     */
    @Transactional(readOnly = true)
    public HospitalDTO getHospitalDetail(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        return HospitalDTO.fromEntity(hospital);
    }

    @Transactional(readOnly = true)
    public List<HospitalDepartment> getDepartments(Long hospitalId) {
        return departmentRepository.findByHospital_HospitalId(hospitalId);
    }

    @Transactional(readOnly = true)
    public List<HospitalInstitution> getInstitutions(Long hospitalId) {
        return institutionRepository.findByHospital_HospitalId(hospitalId);
    }

    @Transactional(readOnly = true)
    public List<FacilityBusinessHourDTO> getFacilityBusinessHoursByHospitalId(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));

        return hospital.getFacility().getBusinessHours()
                .stream()
                .map(FacilityBusinessHourDTO::fromEntity)
                .toList();
    }

}
