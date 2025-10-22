package code.project.controller;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import code.project.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    // /project/facility/list
    // 병원 or 약국 전체 목록 조회 (type 기반, 페이징)
    @GetMapping("/list")
    public ResponseEntity<Page<Facility>> getFacilities(
            @RequestParam FacilityType type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Facility> facilities = facilityService.getFacilities(type, keyword, page, size);
        return ResponseEntity.ok(facilities);
    }

    // /project/facility/{id}
    // 단일 시설 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<Facility> getFacilityDetail(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.getFacility(id));
    }
}
