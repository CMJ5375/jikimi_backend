package code.project.controller;

import code.project.dto.HospitalDTO;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/project/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    // 병원 검색 API (거리순 + 키워드 + 과목CSV + 기관유형 + 응급실)
    @GetMapping("/search")
    public Page<HospitalDTO> searchHospitals(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String org,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) Boolean emergency,
            @RequestParam(required = false, defaultValue = "37.432764") Double lat,
            @RequestParam(required = false, defaultValue = "127.129637") Double lng,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyFavorites,
            Authentication authentication,
            Pageable pageable
    ) {
        String username = (onlyFavorites && authentication != null) ? authentication.getName() : null;
        return hospitalService.searchHospitals(
                keyword, org, dept, emergency, lat, lng, onlyFavorites, username, pageable
        );
    }

    // 병원 목록 조회 (페이징)
    @GetMapping("/list")
    public ResponseEntity<Page<HospitalDTO>> getHospitals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(hospitalService.getHospitalList(page, size));
    }

    @GetMapping
    public Page<HospitalDTO> listHospitals(Pageable pageable) {
        return hospitalService.getHospitals(pageable);
    }

    // 병원 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<HospitalDTO> getHospitalDetail(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospitalDetail(id));
    }

    // 특정 병원의 요일별 영업시간 (= Facility의 영업시간)
    @GetMapping("/{id}/business-hours")
    public ResponseEntity<List<FacilityBusinessHourDTO>> getBusinessHours(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getFacilityBusinessHoursByHospitalId(id));
    }

    // 특정 병원의 진료과목 목록 (CSV → List<String>)
    @GetMapping("/{id}/departments")
    public ResponseEntity<List<String>> getDepartments(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getDepartments(id));
    }

    // 특정 병원의 의료자원 목록 (CSV → List<String>)
    @GetMapping("/{id}/institutions")
    public ResponseEntity<List<String>> getInstitutions(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getInstitutions(id));
    }
}
