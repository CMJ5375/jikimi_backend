package code.project.controller;

import code.project.domain.*;
import code.project.dto.HospitalDTO;
import code.project.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/project/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    // /project/hospital/list
    // 병원 목록 조회 (페이징)
    @GetMapping("/list")
    public ResponseEntity<Page<HospitalDTO>> getHospitals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(hospitalService.getHospitalList(page, size));
    }

    // /project/hospital/{id}
    // 병원 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<HospitalDTO> getHospitalDetail(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getHospitalDetail(id));
    }

    // /project/hospital/{id}/departments
    // 특정 병원의 진료과목 목록 조회
    @GetMapping("/{id}/departments")
    public ResponseEntity<List<HospitalDepartment>> getDepartments(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getDepartments(id));
    }

    // /project/hospital/{id}/business-hours
    // 특정 병원의 요일별 진료시간 조회
    @GetMapping("/{id}/business-hours")
    public ResponseEntity<List<HospitalBusinessHour>> getBusinessHours(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getBusinessHours(id));
    }

    // /project/hospital/{id}/institutions
    // 특정 병원의 의료자원 목록 조회
    @GetMapping("/{id}/institutions")
    public ResponseEntity<List<HospitalInstitution>> getInstitutions(@PathVariable Long id) {
        return ResponseEntity.ok(hospitalService.getInstitutions(id));
    }
}
