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

    // 내 즐겨찾기 ID 목록 (타입별)
    @GetMapping("/my")
    public ResponseEntity<List<Long>> myFavorites(
            Authentication authentication,
            @RequestParam FacilityType type // HOSPITAL | PHARMACY
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(favoriteService.getMyFavoriteFacilityIds(username, type));
    }

    // 단건 즐겨찾기 여부
    @GetMapping("/check/{facilityId}")
    public ResponseEntity<Boolean> isFavorite(
            Authentication authentication,
            @PathVariable Long facilityId
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(favoriteService.isMyFavorite(username, facilityId));
    }

    // 추가
    @PostMapping("/{facilityId}")
    public ResponseEntity<Void> add(
            Authentication authentication,
            @PathVariable Long facilityId
    ) {
        String username = authentication.getName();
        favoriteService.addFavorite(username, facilityId);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{facilityId}")
    public ResponseEntity<Void> remove(
            Authentication authentication,
            @PathVariable Long facilityId
    ) {
        String username = authentication.getName();
        favoriteService.removeFavorite(username, facilityId);
        return ResponseEntity.noContent().build();
    }
}
