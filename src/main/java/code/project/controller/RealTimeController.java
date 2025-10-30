// src/main/java/code/project/controller/RealTimeController.java
package code.project.controller;

import code.project.external.HiraEmergencyApiClient;
import code.project.external.PharmacyApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project/realtime")
public class RealTimeController {

    private final HiraEmergencyApiClient hira;
    private final PharmacyApiClient pharm;

    @GetMapping("/ping")
    public String ping() { return "pong"; }

    // ===== 병원(HIRA) =====
    @GetMapping("/hira/hospitals")
    public Mono<ResponseEntity<Map>> getHospitals(
            @RequestParam String q0,
            @RequestParam String q1,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return hira.getHospitalList(q0, q1, page, size).map(ResponseEntity::ok);
    }

    @GetMapping("/hira/hospitals/raw")
    public Mono<ResponseEntity<String>> getHospitalsRaw(
            @RequestParam String q0,
            @RequestParam String q1,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return hira.getHospitalListRaw(q0, q1, page, size)
                .map(body -> ResponseEntity.ok().body(body));
    }

    // ===== 약국(별도 기관/키) =====
    @GetMapping("/pharm/pharmacies")
    public Mono<ResponseEntity<Map>> getPharmacies(
            @RequestParam String q0,
            @RequestParam String q1,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return pharm.getPharmacyList(q0, q1, page, size).map(ResponseEntity::ok);
    }

    @GetMapping("/pharm/pharmacies/raw")
    public Mono<ResponseEntity<String>> getPharmaciesRaw(
            @RequestParam String q0,
            @RequestParam String q1,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return pharm.getPharmacyListRaw(q0, q1, page, size)
                .map(body -> ResponseEntity.ok().body(body));
    }
}
