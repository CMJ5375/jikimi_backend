package code.project.controller;

import code.project.domain.Pharmacy;
import code.project.dto.PharmacyDTO;
import code.project.service.PharmacyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project/pharmacy")
@RequiredArgsConstructor
public class PharmacyController {

    // /project/pharmacy/list
    // 약국 목록 조회 (페이징)
    private final PharmacyService pharmacyService;

    @GetMapping("/list")
    public ResponseEntity<Page<PharmacyDTO>> getPharmacies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(pharmacyService.getPharmacyList(page, size));
    }

    // /project/pharmacy/{id}
    // 약국 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PharmacyDTO> getPharmacyDetail(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getPharmacyDetail(id));
    }
}
