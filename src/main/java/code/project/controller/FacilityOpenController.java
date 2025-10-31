// src/main/java/code/project/controller/FacilityOpenController.java
package code.project.controller;

import code.project.service.OpenStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project/facility")
public class FacilityOpenController {

    private final OpenStatusService openStatusService;

    /** 단건: /project/facility/{facilityId}/open => { facilityId, open } */
    @GetMapping("/{facilityId}/open")
    public ResponseEntity<Map<String, Object>> getOpen(@PathVariable Long facilityId) {
        boolean open = openStatusService.isFacilityOpen(facilityId);
        return ResponseEntity.ok(Map.of("facilityId", facilityId, "open", open));
    }

    /** 배치: { "facilityIds": [1,2,3] } => { 1:true, 2:false, 3:true } */
    @PostMapping("/open-batch")
    public ResponseEntity<Map<Long, Boolean>> getOpenBatch(@RequestBody Map<String, List<Long>> body) {
        List<Long> ids = (body == null) ? Collections.emptyList() : body.getOrDefault("facilityIds", Collections.emptyList());
        Map<Long, Boolean> result = openStatusService.batchIsOpen(ids);
        return ResponseEntity.ok(result);
    }
}
