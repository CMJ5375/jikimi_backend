package code.project.service;

import code.project.domain.Pharmacy;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.dto.PharmacyDTO;
import code.project.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    /**
     * 일반 약국 검색 (거리와 키워드로 검색, 즐겨찾기 포함 여부)
     * @param keyword 키워드
     * @param lat 위도
     * @param lng 경도
     * @param distance 거리
     * @param onlyFavorites 즐겨찾기 여부
     * @param username 사용자 이름
     * @param pageable 페이지 정보
     * @return 검색된 약국 페이지
     */
    // PharmacyService 수정된 부분
    @Transactional(readOnly = true)
    public Page<PharmacyDTO> searchPharmacies(
            String keyword,
            Double lat,
            Double lng,
            String distance,  // distance 값 (ex. 1km, 500m)
            Boolean onlyFavorites,
            String username,
            Pageable pageable
    ) {
        Double radiusKm = null;

        // 거리 값 처리 (미터 -> km 변환)
        if (distance != null && !distance.isBlank()) {
            String s = distance.trim().toLowerCase();
            log.info("정규화된 거리 문자열: '{}'", s);
            try {
                if (s.endsWith("km")) {                      // ✅ km 먼저
                    String num = s.substring(0, s.length() - 2).trim();
                    radiusKm = Double.parseDouble(num);      // km 그대로
                } else if (s.endsWith("m")) {                // ✅ 그 다음 m
                    String num = s.substring(0, s.length() - 1).trim();
                    radiusKm = Double.parseDouble(num) / 1000.0; // m → km
                } else {                                     // 숫자만 온 경우 m로 간주
                    radiusKm = Double.parseDouble(s) / 1000.0;
                }
            } catch (NumberFormatException e) {
                log.warn("잘못된 거리 값: '{}'", distance, e);
            }
        }
        log.info("[PharmacyService] 거리 파싱 직전: distance='{}'", distance);
        log.info("[PharmacyService] 거리 변환 결과 radiusKm: {}", radiusKm);


        // 즐겨찾기만 찾는 경우, 또는 전체 검색인 경우 구분
        Page<Pharmacy> page;
        if (onlyFavorites) {
            page = pharmacyRepository.searchFavoritePharmaciesWithin(username, keyword, lat, lng, radiusKm, pageable);
        } else if (radiusKm != null) {
            page = pharmacyRepository.searchPharmaciesWithin(keyword, lat, lng, radiusKm, pageable);
        } else {
            page = pharmacyRepository.searchPharmaciesAll(keyword, pageable);
        }

        return page.map(PharmacyDTO::fromEntity);
    }

    /**
     * 관리자용 약국 목록 조회
     * @param pageable 페이지 정보
     * @return 약국 목록 페이지
     */
    @Transactional(readOnly = true)
    public Page<PharmacyDTO> getPharmacies(Pageable pageable) {
        Page<Pharmacy> page = pharmacyRepository.findAllWithFacility(pageable);
        return page.map(PharmacyDTO::fromEntity);
    }

    /**
     * 관리자용 약국 목록 조회 (페이징)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 약국 목록 페이지
     */
    @Transactional(readOnly = true)
    public Page<PharmacyDTO> getPharmacyList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("pharmacyId").descending());
        Page<Pharmacy> list = pharmacyRepository.findAllWithFacility(pageable);
        return list.map(PharmacyDTO::fromEntity);
    }

    /**
     * 약국 상세 조회
     * @param id 약국 ID
     * @return 약국 상세 DTO
     */
    @Transactional(readOnly = true)
    public PharmacyDTO getPharmacyDetail(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findByIdWithFacility(id)
                .orElseThrow(() -> new IllegalArgumentException("약국을 찾을 수 없습니다. 약국: " + id));
        return PharmacyDTO.fromEntity(pharmacy);
    }

    /**
     * 약국의 요일별 영업시간 조회
     * @param id 약국 ID
     * @return 영업시간 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<FacilityBusinessHourDTO> getFacilityBusinessHoursByPharmacyId(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findByIdWithFacility(id)
                .orElseThrow(() -> new IllegalArgumentException("약국을 찾을 수 없습니다. 약국: " + id));
        return pharmacy.getFacility().getBusinessHours()
                .stream().map(FacilityBusinessHourDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Helper Methods

    /**
     * 문자열을 숫자로 변환
     * @param o 변환할 객체
     * @return 변환된 숫자
     */
    private static Double toDouble(Object o) {
        if (o == null) return null;
        if (o instanceof Double d) return d;
        if (o instanceof Float f) return (double) f;
        if (o instanceof Number n) return n.doubleValue();
        if (o instanceof String s && !s.isBlank()) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException ignore) {
            }
        }
        return null;
    }

    /**
     * 소수점 둘째 자리까지 반올림
     * @param v 변환할 값
     * @return 반올림된 값
     */
    private static Double round2(Double v) {
        if (v == null) return null;
        return Math.round(v * 100.0) / 100.0;
    }
}
