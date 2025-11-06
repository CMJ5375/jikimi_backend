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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
    public ResponseEntity<Long> create(
            @PathVariable String type,
            @RequestParam("adminId") Long adminId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        String fileName = null; // <- 한번만 선언
        String fileUrl  = null; // <- 한번만 선언

        if (file != null && !file.isEmpty()) {
            // 저장 폴더: {user.home}/app-uploads/support
            Path uploadDir = Paths.get(System.getProperty("user.home"), "app-uploads", "support");
            Files.createDirectories(uploadDir);

            fileName = file.getOriginalFilename();      // <-- 재선언 금지 (String 제거)
            Path target = uploadDir.resolve(fileName);
            file.transferTo(target.toFile());

            // URL에 들어갈 파일명은 인코딩해서 저장
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            fileUrl = "/uploads/support/" + encoded;    // <-- 재선언 금지 (String 제거)
        }

        JSupportDTO dto = JSupportDTO.builder()
                .title(title)
                .content(content)
                .fileName(fileName)   // 화면 표시용 원본명
                .fileUrl(fileUrl)     // 링크용(인코딩됨)
                .type(type.toLowerCase()) // 서비스에서 소문자로 쓰니 맞춤
                .build();

        Long createdId = service.create(dto, adminId);
        return ResponseEntity.ok(createdId);
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

    // 상단 고정 리스트(다른 페이지에서도 보이게 할것)
    @GetMapping("/{type}/pinned")
    public ResponseEntity<List<JSupportDTO>> pinnedList(@PathVariable String type) {
        type = type.toUpperCase();
        List<JSupportDTO> list = service.getPinnedList(type);
        return ResponseEntity.ok(list);
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

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception {
        // 1) 게시글 조회 (조회수 증가 X)
        JSupportDTO dto = service.get(id, false);
        if (dto.getFileName() == null || dto.getFileName().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        // 2) 저장 폴더(작성 시와 동일 경로)
        Path uploadDir = Paths.get(System.getProperty("user.home"), "app-uploads", "support");
        Path path = uploadDir.resolve(dto.getFileName());

        Resource file = new UrlResource(path.toUri());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // 3) 강제 다운로드 헤더
        String encoded = UriUtils.encode(dto.getFileName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(file);
    }
}
