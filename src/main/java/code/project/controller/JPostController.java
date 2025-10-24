package code.project.controller;

import code.project.domain.JPost;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.JPostDTO;
import code.project.repository.JPostRepository;
import code.project.service.JPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class JPostController {

    private final JPostService JPostService;
    private final JPostRepository JPostRepository;


    // 목록 (페이징)
    @GetMapping("/list")
    public PageResponseDTO<JPostDTO> list(PageRequestDTO pageRequestDTO) {
        return JPostService.getList(pageRequestDTO);
    }

    // 단건 조회 => 조회수증가
    @GetMapping("/{postId}")
    public JPostDTO get(@PathVariable Long postId) {
        JPostService.incrementView(postId); // 조회수 1 증가
        return JPostService.get(postId);
    }

    // 등록
    @PostMapping("/add")
    public Long register(@RequestBody JPostDTO dto) {
        return JPostService.register(dto);
    }

    // 수정
    @PutMapping("/{postId}")
    public void modify(@PathVariable Long postId, @RequestBody JPostDTO dto) {
        dto.setPostId(postId);
        JPostService.modify(dto);
    }

    // 삭제
    @DeleteMapping("/{postId}")
    public void remove(@PathVariable Long postId) {
        JPostService.remove(postId);
    }

    // 조회수 증가용
    @PatchMapping("/{id}/views")
    public ResponseEntity<Map<String, Object>> incrementViews(@PathVariable Long id) {
        // 1) 조회수 +1
        JPostService.incrementView(id);

        // 2) 증가된 최신 값 다시 조회해서 클라로 돌려주기
        int updated = JPostRepository.findById(id)
                .map(JPost::getViewCount)
                .orElse(0);

        return ResponseEntity.ok(Map.of("viewCount", updated));
    }
}