package code.project.controller;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import code.project.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facilities")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    public ResponseEntity<?> getFacilities(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String orgType,
            @RequestParam(required = false) String deptName
    ) {
        try {
            // type이 없으면 전체 조회
            if (type == null || type.isBlank()) {
                return ResponseEntity.ok(facilityService.getAllFacilities());
            }

            FacilityType facilityType = FacilityType.valueOf(type.toUpperCase());
            List<Facility> result = facilityService.searchFacilities(facilityType, name, orgType, deptName);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public Facility getFacility(@PathVariable Long id) {
        return facilityService.getFacilityById(id)
                .orElseThrow(() -> new RuntimeException("Facility not found with id " + id));
    }
}
