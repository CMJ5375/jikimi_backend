package code.project.service;

import code.project.domain.JPost;
import code.project.domain.JUser;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.JPostDTO;

public interface JPostService {

    // 단건 조회
    JPostDTO get(Long postId);

    // 등록
    Long register(JPostDTO dto);

    // 수정
    void modify(JPostDTO dto);

    // 삭제(소프트 딜리트라면 isDeleted = true 처리)
    void remove(Long postId);

    // 페이징 목록
    PageResponseDTO<JPostDTO> getList(PageRequestDTO pageRequestDTO);

    // 조회수
    void incrementView(Long id);

    // ====== Mapper ======

    // Entity -> DTO
    default JPostDTO entityToDTO(JPost p) {
        if (p == null) return null;

        String authorName = null;
        if (p.getUser() != null) {
            // username, name 등 중 하나가 비어있으면 다른 걸로 보완
            String u1 = p.getUser().getUsername();
            String u2 = p.getUser().getName();
            authorName = (u2 != null && !u2.isBlank())
                    ? u2
                    : (u1 != null ? u1 : null);
        }

        return JPostDTO.builder()
                .postId(p.getPostId())
                .title(p.getTitle())
                .boardCategory(p.getBoardCategory())
                .content(p.getContent())
                .fileUrl(p.getFileUrl())
                .likeCount(p.getLikeCount())
                .viewCount(p.getViewCount())
                .createdAt(p.getCreatedAt())
                .isDeleted(p.getIsDeleted())
                .userId(p.getUser() != null ? p.getUser().getUserId() : null)
                .authorName(authorName) // 추가
                .build();
    }

    // DTO -> Entity
    //  - 연관 엔티티는 ID만 채운 프록시로 세팅(서비스 구현에서 getReferenceById로 치환해도 OK)
    default JPost dtoToEntity(JPostDTO dto) {
        if (dto == null) return null;

        JPost.JPostBuilder builder = JPost.builder()
                .postId(dto.getPostId())
                .title(dto.getTitle())
                .boardCategory(dto.getBoardCategory())
                .content(dto.getContent())
                .fileUrl(dto.getFileUrl())
                .likeCount(dto.getLikeCount())
                .viewCount(dto.getViewCount())
                .createdAt(dto.getCreatedAt())
                .isDeleted(dto.getIsDeleted());

        if (dto.getUserId() != null) {
            builder.user(JUser.builder().userId(dto.getUserId()).build());
        }

        return builder.build();
    }
}