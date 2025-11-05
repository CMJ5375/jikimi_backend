package code.project.service;

import code.project.domain.JComment;
import code.project.domain.JPost;
import code.project.domain.JUser;
import code.project.dto.JCommentDTO;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;

public interface JCommentService {

    // 목록 (DESC 정렬은 구현부에서 Pageable로 적용)
    PageResponseDTO<JCommentDTO> getList(Long postId, PageRequestDTO pageRequestDTO);

    // 단건 조회
    JCommentDTO get(Long commentId);

    // 등록: 경로의 postId와 로그인 사용자(username)으로 강제
    Long register(Long postId, String username, JCommentDTO dto);

    // 수정: 본인만
    void modify(Long commentId, String username, JCommentDTO dto);

    // 삭제: 본인만
    void remove(Long commentId, String username);

    //내 댓글 목록
    PageResponseDTO<JCommentDTO> getMyComments(String username, PageRequestDTO req);
    // ====== Mapper ======
    default JCommentDTO entityToDTO(JComment c) {
        if (c == null) return null;

        String authorName = "익명";
        String authorProfileImage = "/default-profile.png";

        if (c.getUser() != null) {
            var u = c.getUser();
            if (u.getName() != null && !u.getName().isBlank()) {
                authorName = u.getName();
            } else if (u.getUsername() != null && !u.getUsername().isBlank()) {
                authorName = u.getUsername();
            } else if (u.getEmail() != null && !u.getEmail().isBlank()) {
                authorName = u.getEmail().split("@")[0];
            }

            if (u.getProfileImage() != null && !u.getProfileImage().isBlank()) {
                authorProfileImage = u.getProfileImage();
            }
        }

        return JCommentDTO.builder()
                .commentId(c.getCommentId())
                .postId(c.getPost() != null ? c.getPost().getPostId() : null)
                .userId(c.getUser() != null ? c.getUser().getUserId() : null)
                .authorName(authorName)
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .authorProfileImage(authorProfileImage)
                .build();
    }

    default JComment dtoToEntity(JCommentDTO dto) {
        if (dto == null) return null;

        JComment.JCommentBuilder b = JComment.builder()
                .commentId(dto.getCommentId())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt());

        if (dto.getPostId() != null) {
            b.post(JPost.builder().postId(dto.getPostId()).build());
        }
        if (dto.getUserId() != null) {
            b.user(JUser.builder().userId(dto.getUserId()).build());
        }
        return b.build();
    }
}
