// code.project.controller.PostController
package code.project.controller;

import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.JPostDTO;
import code.project.service.JPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final JPostService JPostService;

    // 목록 (페이징)
    @GetMapping("/list")
    public PageResponseDTO<JPostDTO> list(PageRequestDTO pageRequestDTO) {
        return JPostService.getList(pageRequestDTO);
    }

    // 단건 조회
    @GetMapping("/{postId}")
    public JPostDTO get(@PathVariable Long postId) {
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
}