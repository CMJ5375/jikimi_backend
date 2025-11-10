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
import java.security.Principal; // ✅ 추가
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project/support")
@RequiredArgsConstructor
@Slf4j
public class JSupportController {

    private final JSupportService service;

    @GetMapping("/{type}/list")
    public Page<JSupportDTO> list(@PathVariable String type,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        type = type.toUpperCase();
        return service.list(type, keyword, page, size);
    }

    @GetMapping("/{type}/{id}")
    public JSupportDTO get(@PathVariable String type, @PathVariable Long id) {
        type = type.toUpperCase();
        return service.get(id, true);
    }

    // ✅ 글쓰기: adminId를 더이상 요청 파라미터로 요구하지 않음.
    // Principal(username) → service.resolveAdminIdByUsername(username) 로 숫자 ID 조회
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @PostMapping(value = "/{type}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(
            @PathVariable String type,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "file", required = false) MultipartFile file,
            Principal principal // ✅ 표준 Principal 사용
    ) throws IOException {

        if (principal == null || principal.getName() == null) {
            log.warn("[support/create] principal null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "인증 정보가 없습니다."));
        }

        final String username = principal.getName();
        log.debug("[support/create] username={}", username);

        Long adminId;
        try {
            adminId = service.resolveAdminIdByUsername(username); // ✅ 숫자 ID 조회
        } catch (Exception e) {
            log.warn("[support/create] resolve adminId fail. username={}", username, e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "관리자 권한 또는 계정을 확인하세요."));
        }

        // 파일 저장
        String fileName = null;
        String fileUrl  = null;
        if (file != null && !file.isEmpty()) {
            Path uploadDir = Paths.get(System.getProperty("user.home"), "app-uploads", "support");
            Files.createDirectories(uploadDir);

            fileName = file.getOriginalFilename();
            Path target = uploadDir.resolve(fileName);
            file.transferTo(target.toFile());

            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            fileUrl = "/uploads/support/" + encoded;
        }

        // 저장
        JSupportDTO dto = JSupportDTO.builder()
                .title(title)
                .content(content)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .type(type.toLowerCase())
                .build();

        Long createdId = service.create(dto, adminId);
        return ResponseEntity.ok(createdId);
    }

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

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @DeleteMapping("/{type}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String type,
                                       @PathVariable Long id,
                                       @RequestParam Long adminId) {
        type = type.toUpperCase();
        service.delete(id, adminId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @PostMapping("/{type}/{id}/pin")
    public ResponseEntity<Void> pin(@PathVariable String type,
                                    @PathVariable Long id,
                                    @RequestParam Long adminId) {
        type = type.toUpperCase();
        service.pin(id, adminId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ADMIN')")
    @DeleteMapping("/{type}/{id}/unpin")
    public ResponseEntity<Void> unpin(@PathVariable String type,
                                      @PathVariable Long id,
                                      @RequestParam Long adminId) {
        service.unpin(id, adminId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{type}/pinned")
    public ResponseEntity<List<JSupportDTO>> pinnedList(@PathVariable String type) {
        type = type.toUpperCase();
        List<JSupportDTO> list = service.getPinnedList(type);
        return ResponseEntity.ok(list);
    }

    // (like 관련 메서드/다운로드 메서드는 기존 그대로)
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception {
        JSupportDTO dto = service.get(id, false);
        if (dto.getFileName() == null || dto.getFileName().isBlank()) {
            return ResponseEntity.notFound().build();
        }
        Path uploadDir = Paths.get(System.getProperty("user.home"), "app-uploads", "support");
        Path path = uploadDir.resolve(dto.getFileName());

        Resource file = new UrlResource(path.toUri());
        if (!file.exists()) return ResponseEntity.notFound().build();

        String encoded = UriUtils.encode(dto.getFileName(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(file);
    }
}
