package code.project.service;

import code.project.domain.FacilityBusinessHour;
import code.project.domain.Pharmacy;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.dto.PharmacyDTO;
import code.project.repository.PharmacyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    private Double toKm(String distance) {
        if (distance == null) return null;
        String s = distance.trim().toLowerCase();
        if (s.isEmpty()) return null;
        try {
            if (s.endsWith("km")) {
                return Double.parseDouble(s.substring(0, s.length() - 2).trim());
            }
            if (s.endsWith("m")) {
                double meters = Double.parseDouble(s.substring(0, s.length() - 1).trim());
                return meters / 1000.0;
            }
            // 숫자만 온 경우 km로 간주
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null; // 파싱 실패하면 전체 조회
        }
    }

    // 거리 + 키워드 기반 검색
    public Page<PharmacyDTO> searchPharmacies(String keyword, double lat, double lng, String distance, Pageable pageable) {
        Double radiusKm = toKm(distance);
        Page<Pharmacy> page = pharmacyRepository.searchPharmacies(keyword, lat, lng, radiusKm, pageable);
        return page.map(PharmacyDTO::fromEntity);
    }

    // 약국 목록 (페이징)
    public Page<PharmacyDTO> getPharmacyList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("pharmacyId").descending());
        Page<Pharmacy> pharmacyPage = pharmacyRepository.findAll(pageable);
        return pharmacyPage.map(PharmacyDTO::fromEntity);
    }

    // 약국 상세 조회
    public PharmacyDTO getPharmacyDetail(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 약국 정보를 찾을 수 없습니다."));
        return PharmacyDTO.fromEntity(pharmacy);
    }

    // 영업시간 조회
    public List<FacilityBusinessHourDTO> getFacilityBusinessHoursByPharmacyId(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("약국 정보를 찾을 수 없습니다."));
        List<FacilityBusinessHour> hours = pharmacy.getFacility().getBusinessHours();
        if (hours == null) return List.of();
        return hours.stream()
                .map(FacilityBusinessHourDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
