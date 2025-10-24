package code.project.service;

import code.project.domain.JComment;
import code.project.dto.JCommentDTO;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.repository.JCommentRepository;
import code.project.repository.JPostRepository;
import code.project.repository.JUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class JCommentServiceImpl implements JCommentService {

    private final JCommentRepository commentRepository;
    private final JPostRepository postRepository;
    private final JUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<JCommentDTO> getList(Long postId, PageRequestDTO req) {
        Pageable pageable = PageRequest.of(
                Math.max(req.getPage() - 1, 0),
                req.getSize(),
                Sort.by(Sort.Direction.DESC, "commentId") // 최신순 DESC
        );

        var page = commentRepository.findByPost_PostId(postId, pageable);
        var dtoList = page.map(this::entityToDTO).getContent();

        return PageResponseDTO.<JCommentDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(req)
                .totalCount(page.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public JCommentDTO get(Long commentId) {
        JComment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found: " + commentId));
        return entityToDTO(c);
    }

    @Override
    public Long register(Long postId, String username, JCommentDTO dto) {
        // 서버에서 게시글/작성자 강제 세팅
        var entity = dtoToEntity(dto);
        entity.setPost(postRepository.getReferenceById(postId));
        entity.setUser(userRepository.getCodeUserByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username)));

        var saved = commentRepository.save(entity);
        return saved.getCommentId();
    }

    @Override
    public void modify(Long commentId, String username, JCommentDTO dto) {
        JComment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found: " + commentId));

        // 본인만 수정
        if (!c.getUser().getUsername().equals(username)) {
            throw new SecurityException("본인 댓글만 수정할 수 있습니다.");
        }

        if (dto.getContent() != null) {
            c.setContent(dto.getContent());
        }
        // 작성자/게시글 변경은 금지
        commentRepository.save(c);
    }

    @Override
    public void remove(Long commentId, String username) {
        JComment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found: " + commentId));

        // 본인만 삭제
        if (!c.getUser().getUsername().equals(username)) {
            throw new SecurityException("본인 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(c);
        // 소프트 삭제 원하면 여기서 본문 마스킹 + 플래그 처리(엔티티 필드 추가 필요)
    }
}
