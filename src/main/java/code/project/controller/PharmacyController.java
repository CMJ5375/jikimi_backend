package code.project.controller;

import code.project.dto.FacilityBusinessHourDTO;
import code.project.dto.PharmacyDTO;
import code.project.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/project/pharmacy")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyService pharmacyService;

    // 약국 검색 API (거리순 + 키워드 + 위치 + 즐겨찾기 + 페이징)
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
        final String username = (onlyFavorites && authentication != null) ? authentication.getName() : null;

        log.info("[CTRL][IN] kw='{}' lat={} lng={} dist='{}' onlyFav={} user='{}' page={}/{}",
                keyword, lat, lng, distance, onlyFavorites, username,
                pageable.getPageNumber(), pageable.getPageSize());

        Page<PharmacyDTO> page = pharmacyService.searchPharmacies(
                keyword, lat, lng, distance, onlyFavorites, username, pageable
        );

        log.info("[CTRL][OUT] totalElements={} totalPages={}", page.getTotalElements(), page.getTotalPages());
        return page;
    }

    @GetMapping("/list")
    public ResponseEntity<Page<PharmacyDTO>> getPharmacies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(pharmacyService.getPharmacyList(page, size));
    }

    @GetMapping
    public Page<PharmacyDTO> listPharmacies(Pageable pageable) {
        return pharmacyService.getPharmacies(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PharmacyDTO> getPharmacyDetail(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPharmacyDetail(id));
    }

    @GetMapping("/{id}/business-hours")
    public ResponseEntity<List<FacilityBusinessHourDTO>> getPharmacyBusinessHours(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getFacilityBusinessHoursByPharmacyId(id));
    }
}