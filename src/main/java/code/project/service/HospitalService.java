package code.project.service;

import code.project.domain.Hospital;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.dto.HospitalDTO;
import code.project.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    // 병원 검색 서비스 로직 (거리순 + 키워드 + 과목CSV + 기관유형 + 응급실여부)
    @Transactional(readOnly = true)
    public Page<HospitalDTO> searchHospitals(
            String keyword,
            String org,
            String dept,
            Boolean emergency,
            double lat,
            double lng,
            Pageable pageable
    ) {
        Page<Hospital> hospitals = hospitalRepository.searchHospitals(keyword, dept, org, emergency, lat, lng, pageable);
        return hospitals.map(HospitalDTO::fromEntity);
    }

    // 병원 목록 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<HospitalDTO> getHospitalList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("hospitalName").ascending());
        return hospitalRepository.findAll(pageable).map(HospitalDTO::fromEntity);
    }

    // 병원 상세 조회
    @Transactional(readOnly = true)
    public HospitalDTO getHospitalDetail(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        return HospitalDTO.fromEntity(hospital);
    }

    // 진료과목 목록 (CSV → List<String>)
    @Transactional(readOnly = true)
    public List<String> getDepartments(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        return splitCsv(hospital.getDepartmentsCsv());
    }

    // 보유 장비/기관자원 목록 (CSV → List<String>)
    @Transactional(readOnly = true)
    public List<String> getInstitutions(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        return splitCsv(hospital.getInstitutionsCsv());
    }

    // 진료시간
    @Transactional(readOnly = true)
    public List<FacilityBusinessHourDTO> getFacilityBusinessHoursByHospitalId(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다."));
        return hospital.getFacility().getBusinessHours()
                .stream()
                .map(FacilityBusinessHourDTO::fromEntity)
                .toList();
    }

    // ---------- helpers ----------
    private static String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    private static Double toDouble(Object o) {
        if (o == null) return null;
        if (o instanceof Double d) return d;
        if (o instanceof Float f) return (double) f;
        if (o instanceof Number n) return n.doubleValue();
        if (o instanceof String s && !s.isBlank()) {
            try { return Double.parseDouble(s); } catch (NumberFormatException ignore) {}
        }
        return null;
    }

    private static Double round2(Double v) {
        if (v == null) return null;
        return Math.round(v * 100.0) / 100.0;
    }
}
