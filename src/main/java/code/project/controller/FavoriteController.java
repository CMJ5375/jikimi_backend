package code.project.controller;

import code.project.domain.FacilityType;
import code.project.service.JUserFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final JUserFavoriteService favoriteService;

    // 내 즐겨찾기 ID 목록 (병원, 약국)
    @GetMapping("/my")
    public ResponseEntity<List<Long>> myFavorites(
            Authentication authentication,
            @RequestParam("type") FacilityType type
    ) {
        String username = authentication.getName();
        List<Long> ids = favoriteService.getMyFavoriteIds(username, type);
        return ResponseEntity.ok(ids);
    }

    // 즐겨찾기 추가
    @PostMapping("/{targetId}")
    public ResponseEntity<Void> add(
            Authentication authentication,
            @PathVariable Long targetId,
            @RequestParam("type") FacilityType type
    ) {
        String username = authentication.getName();
        favoriteService.addFavorite(username, type, targetId);
        return ResponseEntity.ok().build();
    }

    // 즐겨찾기 삭제
    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> remove(
            Authentication authentication,
            @PathVariable Long targetId,
            @RequestParam("type") FacilityType type
    ) {
        String username = authentication.getName();
        favoriteService.removeFavorite(username, type, targetId);
        return ResponseEntity.noContent().build();
    }

    // 즐겨찾기 여부 확인 (토글 기능용)
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
}
