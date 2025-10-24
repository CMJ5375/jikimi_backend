package code.project.controller;

import code.project.dto.JCommentDTO;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.service.JCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class JCommentController {

    private final JCommentService commentService;

    // 목록 (DESC 정렬은 서비스에서 Pageable로 적용)
    @GetMapping("/list")
    public PageResponseDTO<JCommentDTO> list(@PathVariable Long postId,
                                             PageRequestDTO pageRequestDTO) {
        return commentService.getList(postId, pageRequestDTO);
    }

    // 단건 조회
    @GetMapping("/{commentId}")
    public JCommentDTO get(@PathVariable Long postId,
                           @PathVariable Long commentId) {
        return commentService.get(commentId);
    }

    // 등록: 로그인 사용자만 가능, 경로의 postId 강제
    @PostMapping("/add")
    public Long register(@PathVariable Long postId,
                         @RequestBody JCommentDTO dto,
                         Principal principal) {
        String username = principal.getName();
        return commentService.register(postId, username, dto);
    }

    // 수정: 본인만
    @PutMapping("/{commentId}")
    public void modify(@PathVariable Long postId,
                       @PathVariable Long commentId,
                       @RequestBody JCommentDTO dto,
                       Principal principal) {
        String username = principal.getName();
        dto.setCommentId(commentId);
        commentService.modify(commentId, username, dto);
    }

    // 삭제: 본인만
    @DeleteMapping("/{commentId}")
    public void remove(@PathVariable Long postId,
                       @PathVariable Long commentId,
                       Principal principal) {
        String username = principal.getName();
        commentService.remove(commentId, username);
    }
}
