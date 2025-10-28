package code.project.controller;

import code.project.dto.FacilityBusinessHourDTO;
import code.project.dto.PharmacyDTO;
import code.project.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project/pharmacy")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyService pharmacyService;

    // 약국 검색 API
    @GetMapping("/search")
    public Page<PharmacyDTO> searchPharmacies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "37.432764") Double lat,
            @RequestParam(required = false, defaultValue = "127.129637") Double lng,
            @RequestParam(required = false) String distance,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyFavorites,
            Authentication authentication,
            Pageable pageable
    ) {
        String username = (onlyFavorites && authentication != null) ? authentication.getName() : null;
        return pharmacyService.searchPharmacies(keyword, lat, lng, distance, onlyFavorites, username, pageable);
    }

    // 약국 목록 조회 (페이징)
    @GetMapping("/list")
    public ResponseEntity<Page<PharmacyDTO>> getPharmacies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(pharmacyService.getPharmacyList(page, size));
    }

    // 관리자용
    @GetMapping
    public Page<PharmacyDTO> listPharmacies(Pageable pageable) {
        return pharmacyService.getPharmacies(pageable);
    }

    // 약국 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PharmacyDTO> getPharmacyDetail(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPharmacyDetail(id));
    }

    // 특정 약국의 요일별 영업시간 조회(= Facility의 영업시간)
    @GetMapping("/{id}/business-hours")
    public ResponseEntity<List<FacilityBusinessHourDTO>> getPharmacyBusinessHours(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getFacilityBusinessHoursByPharmacyId(id));
    }
}
