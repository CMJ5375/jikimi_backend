package code.project.controller;

import code.project.service.HospitalListView;
import code.project.service.HospitalQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospitals")
public class HospitalQueryController {

    private final HospitalQueryService service;

    @GetMapping("/list-lite")
    public Page<HospitalListView> listLite(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String org,
            @RequestParam(required = false) Boolean emergency,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @PageableDefault(size = 20, sort = "hospitalId", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return service.getListLite(keyword, dept, org, emergency, lat, lng, pageable);
    }

    @GetMapping("/favorites-lite")
    public Page<HospitalListView> favoriteListLite(
            @RequestParam String username,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String org,
            @RequestParam(required = false) Boolean emergency,
            @PageableDefault(size = 20, sort = "hospitalId", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return service.getFavoriteListLite(username, keyword, dept, org, emergency, pageable);
    }
}
