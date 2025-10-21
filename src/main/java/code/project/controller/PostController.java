// code.project.controller.PostController
package code.project.controller;

import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.PostDTO;
import code.project.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 목록 (페이징)
    public PageResponseDTO<PostDTO> list(PageRequestDTO pageRequestDTO) {
        return postService.getList(pageRequestDTO);
    }

    // 단건 조회
    @GetMapping("/{postId}")
    public PostDTO get(@PathVariable Long postId) {
        return postService.get(postId);
    }

    // 등록
    public Long register(@RequestBody PostDTO dto) {
        return postService.register(dto);
    }

    // 수정
    @PutMapping("/{postId}")
    public void modify(@PathVariable Long postId, @RequestBody PostDTO dto) {
        dto.setPostId(postId);
        postService.modify(dto);
    }

    // 삭제
    @DeleteMapping("/{postId}")
    public void remove(@PathVariable Long postId) {
        postService.remove(postId);
    }
}