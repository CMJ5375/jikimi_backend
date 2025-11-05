package code.project.controller;

import code.project.dto.JSupportDTO;
import code.project.service.JSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/project/support")
@RequiredArgsConstructor
public class JSupportController {

    private final JSupportService service;

    // type: NOTICE / FAQ / DATAROOM
    @GetMapping("/{type}/list")
    public Page<JSupportDTO> list(@PathVariable String type,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return service.list(type.toUpperCase(), keyword, page, size);
    }

    // 조회
    @GetMapping("/{type}/{id}")
    public JSupportDTO get(@PathVariable String type, @PathVariable Long id) {
        return service.get(id, true);
    }

    // 글쓰기
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @PostMapping("/{type}")
    public ResponseEntity<Long> create(@PathVariable String type,
                                       @RequestBody JSupportDTO dto,
                                       @RequestParam(required = false) Long adminId) {
        dto.setType(type.toLowerCase());
        return ResponseEntity.ok(service.create(dto, adminId));
    }

    // 수정
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @PutMapping("/{type}/{id}")
    public ResponseEntity<Void> update(@PathVariable String type,
                                       @PathVariable Long id,
                                       @RequestBody JSupportDTO dto,
                                       @RequestParam Long adminId) {
        dto.setSupportId(id);
        dto.setType(type.toUpperCase());
        service.update(dto, adminId);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @DeleteMapping("/{type}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String type,
                                       @PathVariable Long id,
                                       @RequestParam Long adminId) {
        service.delete(id, adminId);
        return ResponseEntity.ok().build();
    }

    // 상단 고정
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @PostMapping("/{type}/{id}/pin")
    public ResponseEntity<Void> pin(@PathVariable String type,
                                    @PathVariable Long id,
                                    @RequestParam Long adminId) {
        service.pin(id, adminId);
        return ResponseEntity.ok().build();
    }

    // 상단 고정 해제
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @DeleteMapping("/{type}/{id}/unpin")
    public ResponseEntity<Void> unpin(@PathVariable String type,
                                      @PathVariable Long id,
                                      @RequestParam Long adminId) {
        service.unpin(id, adminId);
        return ResponseEntity.ok().build();
    }

    // 좋아요 토글
    @PostMapping("/{type}/{id}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        // principal에서 userId를 가져오거나, RequestParam에서 받음
        Long finalUserId = userId;

        // principal이 있다면 거기서 ID 추출 (UserDetails 커스텀 클래스 사용 시 수정)
        if (finalUserId == null && principal != null) {
            try {
                // principal.getUsername()이 실제 userId(String)이라면 변환
                finalUserId = Long.parseLong(principal.getUsername());
            } catch (NumberFormatException ignore) {
                // username이 실제 userId가 아닐 수도 있음 (그럼 프론트에서 userId를 보내야 함)
            }
        }

        if (finalUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        var result = service.toggleSupportLike(id, finalUserId);
        return ResponseEntity.ok(Map.of(
                "likeCount", result.getLikeCount(),
                "liked", result.isLiked()
        ));
    }

    // 좋아요 상태 조회
    @GetMapping("/{type}/{id}/like/status")
    public ResponseEntity<?> likeStatus(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestParam(required = false) Long userId,
            @AuthenticationPrincipal UserDetails principal
    ) {
        Long finalUserId = userId;

        if (finalUserId == null && principal != null) {
            try {
                finalUserId = Long.parseLong(principal.getUsername());
            } catch (NumberFormatException ignore) {}
        }

        boolean liked = (finalUserId != null) && service.isSupportLikedByUser(id, finalUserId);
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
