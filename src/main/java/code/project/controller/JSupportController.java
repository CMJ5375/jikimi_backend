package code.project.controller;

import code.project.dto.JSupportDTO;
import code.project.service.JSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
}
