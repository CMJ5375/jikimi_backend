package code.project.controller;

import code.project.dto.FacilityBusinessHourDTO;
import code.project.service.FacilityBusinessHourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project/facility")
@RequiredArgsConstructor
public class FacilityBusinessHourController {

    private final FacilityBusinessHourService service;

    // 단일 조회 기능만 유지
    @GetMapping("/{facilityId}/business-hours")
    public ResponseEntity<List<FacilityBusinessHourDTO>> getBusinessHours(@PathVariable Long facilityId) {
        return ResponseEntity.ok(service.getBusinessHours(facilityId));
    }
}
