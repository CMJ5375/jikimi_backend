package code.project.controller;

import code.project.dto.JSupportDTO;
import code.project.service.JSupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
@Slf4j
public class JSupportController {

    private final JSupportService service;

    // type: NOTICE / FAQ / DATAROOM
    @GetMapping("/{type}/list")
    public Page<JSupportDTO> list(@PathVariable String type,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        type = type.toUpperCase();
        return service.list(type, keyword, page, size);
    }

    // 조회
    @GetMapping("/{type}/{id}")
    public JSupportDTO get(@PathVariable String type, @PathVariable Long id) {
        type = type.toUpperCase();
        return service.get(id, true);
    }

    // 글쓰기
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @PostMapping("/{type}")
    public ResponseEntity<Long> create(@PathVariable String type,
                                       @RequestBody JSupportDTO dto,
                                       @RequestParam(required = false) Long adminId) {
        dto.setType(type.toUpperCase());
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
        type = type.toUpperCase();
        service.delete(id, adminId);
        return ResponseEntity.ok().build();
    }

    // 상단 고정
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @PostMapping("/{type}/{id}/pin")
    public ResponseEntity<Void> pin(@PathVariable String type,
                                    @PathVariable Long id,
                                    @RequestParam Long adminId) {
        type = type.toUpperCase();
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
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{type}/{id}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(name = "userId", required = false) Long userIdFromQuery,
            @AuthenticationPrincipal UserDetails principal
    ) {
        type = type.toUpperCase();
        Long finalUserId = null;

        if (body != null && body.get("userId") != null) {
            try {
                finalUserId = Long.valueOf(String.valueOf(body.get("userId")));
            } catch (NumberFormatException e) {
                log.warn("Invalid userId in JSON body: {}", body.get("userId"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "userId 형식이 올바르지 않습니다."));
            }
        }

        if (finalUserId == null && userIdFromQuery != null) {
            finalUserId = userIdFromQuery;
        }

        if (finalUserId == null) {
            log.debug("toggleLike called without userId. principal={}", principal != null ? principal.getUsername() : "null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다.", "reason", "userId missing"));
        }

        try {
            var result = service.toggleSupportLike(id, finalUserId);
            return ResponseEntity.ok(Map.of(
                    "likeCount", result.getLikeCount(),
                    "liked", result.isLiked()
            ));
        } catch (IllegalArgumentException e) {
            log.warn("toggleLike bad request. id={}, userId={}, msg={}", id, finalUserId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            log.warn("toggleLike constraint violation. id={}, userId={}", id, finalUserId, e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "이미 처리되었습니다."));
        } catch (Exception e) {
            log.error("toggleLike internal error. id={}, userId={}", id, finalUserId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "서버 오류가 발생했습니다."));
        }
    }

    // 좋아요 상태 조회 (비로그인 허용)
    @GetMapping("/{type}/{id}/like/status")
    public ResponseEntity<?> likeStatus(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestParam(name = "userId", required = false) Long userIdFromQuery,
            @RequestBody(required = false) Map<String, Object> body,
            @AuthenticationPrincipal UserDetails principal
    ) {
        type = type.toUpperCase();
        Long finalUserId = null;

        if (body != null && body.get("userId") != null) {
            try {
                finalUserId = Long.valueOf(String.valueOf(body.get("userId")));
            } catch (NumberFormatException e) {
                log.warn("Invalid userId in likeStatus body: {}", body.get("userId"));
                // 상태 조회는 실패 대신 liked=false로 반환
                return ResponseEntity.ok(Map.of("liked", false));
            }
        }
        if (finalUserId == null && userIdFromQuery != null) {
            finalUserId = userIdFromQuery;
        }

        // userId가 없으면 비로그인 사용자로 간주 → liked=false
        if (finalUserId == null) {
            return ResponseEntity.ok(Map.of("liked", false));
        }

        try {
            boolean liked = service.isSupportLikedByUser(id, finalUserId);
            return ResponseEntity.ok(Map.of("liked", liked));
        } catch (Exception e) {
            log.warn("likeStatus error. id={}, userId={}", id, finalUserId, e);
            return ResponseEntity.ok(Map.of("liked", false));
        }
    }
}
