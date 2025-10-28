package code.project.controller;

import code.project.domain.FacilityType;
import code.project.dto.HospitalDTO;
import code.project.dto.PharmacyDTO;
import code.project.service.JUserFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; /* ✅ 수정됨 */
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final JUserFavoriteService favoriteService;

    // 내 즐겨찾기 ID 리스트 조회 (병원, 약국 탭별로 ID만 가져오기)
    @GetMapping("/my")
    public ResponseEntity<List<Long>> getMyFavoriteIds(
            Authentication authentication,
            @RequestParam("type") FacilityType type
    ) {
        String username = authentication.getName();
        List<Long> ids = favoriteService.getMyFavoriteIds(username, type);
        return ResponseEntity.ok(ids);
    }

    // 즐겨찾기 추가
    @PostMapping("/add/{targetId}")
    public ResponseEntity<Void> addFavorite(
            Authentication authentication,
            @PathVariable Long targetId,
            @RequestParam("type") FacilityType type
    ) {
        String username = authentication.getName();
        favoriteService.addFavorite(username, type, targetId);
        return ResponseEntity.ok().build();
    }

    // 즐겨찾기 삭제
    @DeleteMapping("/remove/{targetId}")
    public ResponseEntity<Void> removeFavorite(
            Authentication authentication,
            @PathVariable Long targetId,
            @RequestParam("type") FacilityType type
    ) {
        String username = authentication.getName();
        favoriteService.removeFavorite(username, type, targetId);
        return ResponseEntity.ok().build();
    }

    // 즐겨찾기 여부 확인 (토글)
    @GetMapping("/check/{targetId}")
    public ResponseEntity<Boolean> checkFavorite(
            Authentication authentication,
            @PathVariable Long targetId,
            @RequestParam("type") FacilityType type
    ) {
        String username = authentication.getName();
        boolean isFav = favoriteService.isFavorite(username, type, targetId);
        return ResponseEntity.ok(isFav);
    }

    // ================================
    // ✅ 마이페이지 전용 즐겨찾기 API
    // ================================

    // 마이페이지 병원 즐겨찾기 목록 (페이징)
    @GetMapping("/hospitals")
    public Page<HospitalDTO> myHospitalFavorites(
            Authentication authentication,
            Pageable pageable
    ) {
        String username = authentication.getName();
        return favoriteService.getMyHospitalFavorites(username, pageable);
    }

    // 마이페이지 약국 즐겨찾기 목록 (페이징)
    @GetMapping("/pharmacies")
    public Page<PharmacyDTO> myPharmacyFavorites(
            Authentication authentication,
            Pageable pageable
    ) {
        String username = authentication.getName();
        return favoriteService.getMyPharmacyFavorites(username, pageable);
    }
}
