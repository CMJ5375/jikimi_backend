package code.project.service;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import code.project.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    // 병원/약국(type)에 따라 시설 목록 조회
    // keyword가 있으면 이름 검색
    // 페이징 및 이름 기준 오름차순 정렬
    public Page<Facility> getFacilities(FacilityType type, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        if (keyword != null && !keyword.isEmpty()) {
            return facilityRepository.findByNameContainingAndType(keyword, type, pageable);
        } else {
            return facilityRepository.findByType(type, pageable);
        }
    }

    // 시설 상세 조회
    public Facility getFacility(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found"));
    }
}
