// src/main/java/code/project/controller/MyCommentController.java
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
@RequestMapping("/api/comments")
public class MyCommentController {

    private final JCommentService commentService;

    // 내 댓글 목록
    @GetMapping("/my")
    public PageResponseDTO<JCommentDTO> myComments(PageRequestDTO pageRequestDTO,
                                                   Principal principal) {
        // 로그인 사용자 기준
        return commentService.getMyComments(principal.getName(), pageRequestDTO);
    }
}
