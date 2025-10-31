// src/main/java/code/project/controller/FacilityBusinessHourController.java
package code.project.controller;

import code.project.domain.FacilityBusinessHour;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.repository.FacilityBusinessHourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/project/facility")
@RequiredArgsConstructor
public class FacilityBusinessHourController {

    private final FacilityBusinessHourRepository facilityBusinessHourRepository;

    /** 항상 200; 데이터 없거나 에러면 [] */
    @GetMapping("/{facilityId}/business-hours")
    @Transactional(readOnly = true)
    public ResponseEntity<List<FacilityBusinessHourDTO>> getFacilityBusinessHours(
            @PathVariable Long facilityId
    ) {
        try {
            List<FacilityBusinessHour> rows =
                    facilityBusinessHourRepository.findByFacility_FacilityIdOrderByIdAsc(facilityId);

            if (rows == null || rows.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<FacilityBusinessHourDTO> dto = rows.stream()
                    .map(FacilityBusinessHourDTO::fromEntity)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            // 어떤 예외가 와도 프론트에는 200 + [] (500 방지)
            return ResponseEntity.ok(Collections.emptyList());
        }
    }
}
