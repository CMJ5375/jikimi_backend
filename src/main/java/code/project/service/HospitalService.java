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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    // 병원 검색 서비스 로직 (거리순 + 키워드 + 과목CSV + 기관유형 + 응급실여부 + 즐겨찾기)
    @Transactional(readOnly = true)
    public Page<HospitalDTO> searchHospitals(
            String keyword,
            String org,
            String dept,
            Boolean emergency,
            Double lat,
            Double lng,
            Boolean onlyFavorites,
            String username,
            Pageable pageable
    ) {
        Page<Hospital> hospitals = onlyFavorites
                ? hospitalRepository.searchFavoriteHospitals(username, keyword, dept, org, emergency, pageable)
                : hospitalRepository.searchHospitals(keyword, dept, org, emergency, lat, lng, pageable);
        return hospitals.map(HospitalDTO::fromEntity);
    }

    // 단순 목록 조회
    @Transactional(readOnly = true)
    public Page<HospitalDTO> getHospitalList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("hospitalId").descending());
        Page<Hospital> list = hospitalRepository.findAllWithFacility(pageable);
        return list.map(HospitalDTO::fromEntity);
    }

    // 병원 상세 조회
    @Transactional(readOnly = true)
    public HospitalDTO getHospitalDetail(Long id) {
        Hospital hospital = hospitalRepository.findByIdWithFacility(id)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다. 병원: " + id));
        return HospitalDTO.fromEntity(hospital);
    }

    // 진료시간
    @Transactional(readOnly = true)
    public List<FacilityBusinessHourDTO> getFacilityBusinessHoursByHospitalId(Long id) {
        Hospital hospital = hospitalRepository.findByIdWithFacility(id)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다. 병원: " + id));
        return hospital.getFacility().getBusinessHours()
                .stream().map(FacilityBusinessHourDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 진료과목 목록
    @Transactional(readOnly = true)
    public List<String> getDepartments(Long id) {
        Hospital hospital = hospitalRepository.findByIdWithFacility(id)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다. 병원: " + id));
        return splitCsv(hospital.getDepartmentsCsv());
    }

    // 의료자원 목록
    @Transactional(readOnly = true)
    public List<String> getInstitutions(Long id) {
        Hospital hospital = hospitalRepository.findByIdWithFacility(id)
                .orElseThrow(() -> new IllegalArgumentException("병원을 찾을 수 없습니다. 병원: " + id));
        return splitCsv(hospital.getInstitutionsCsv());
    }

    // ---------- helpers ----------
    // 문자열 공백과 Null 정리
    private static String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    // CSV 문자열을 List<String>으로 분리
    private static List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    // Object를 Double로 변환
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

    // Double 값을 소수점 둘째 자리까지 반올림
    private static Double round2(Double v) {
        if (v == null) return null;
        return Math.round(v * 100.0) / 100.0;
    }
}
