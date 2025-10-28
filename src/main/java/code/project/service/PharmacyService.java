package code.project.service;

import code.project.domain.Pharmacy;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.dto.PharmacyDTO;
import code.project.repository.PharmacyRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    // 약국 검색 (거리 + 키워드 + 즐겨찾기 전용)
    @Transactional(readOnly = true)
    public Page<PharmacyDTO> searchPharmacies(
            String keyword,
            Double lat,
            Double lng,
            String distance,
            Boolean onlyFavorites,
            String username,
            Pageable pageable
    ) {
        Double radiusKm = null;

        if (distance != null && !distance.isBlank()) {
            if (distance.endsWith("m")) {
                radiusKm = Double.parseDouble(distance.replace("m", "")) / 1000.0;
            } else if (distance.endsWith("km")) {
                radiusKm = Double.parseDouble(distance.replace("km", ""));
            }
        }

        Page<Pharmacy> page = onlyFavorites
                ? pharmacyRepository.searchFavoritePharmacies(username, keyword, pageable)
                : pharmacyRepository.searchPharmacies(keyword, lat, lng, radiusKm, pageable);

        return page.map(PharmacyDTO::fromEntity);
    }

    // 관리자용 상세 조회
    @Transactional(readOnly = true)
    public Page<PharmacyDTO> getPharmacies(Pageable pageable) {
        Page<Pharmacy> page = pharmacyRepository.findAllWithFacility(pageable);
        return page.map(PharmacyDTO::fromEntity);
    }

    // 관리자용 페이지 목록
    @Transactional(readOnly = true)
    public Page<PharmacyDTO> getPharmacyList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("pharmacyId").descending());
        Page<Pharmacy> list = pharmacyRepository.findAllWithFacility(pageable);
        return list.map(PharmacyDTO::fromEntity);
    }

    // 약국 상세 조회
    @Transactional(readOnly = true)
    public PharmacyDTO getPharmacyDetail(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findByIdWithFacility(id)
                .orElseThrow(() -> new IllegalArgumentException("약국을 찾을 수 없습니다. 약국: " + id));
        return PharmacyDTO.fromEntity(pharmacy);
    }

    // 약국 요일별 영업시간
    @Transactional(readOnly = true)
    public List<FacilityBusinessHourDTO> getFacilityBusinessHoursByPharmacyId(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findByIdWithFacility(id)
                .orElseThrow(() -> new IllegalArgumentException("약국을 찾을 수 없습니다. 약국: " + id));
        return pharmacy.getFacility().getBusinessHours()
                .stream().map(FacilityBusinessHourDTO::fromEntity)
                .collect(Collectors.toList());
    }
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
