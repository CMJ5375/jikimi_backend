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

    // ====== Mapper ======

    // Entity -> DTO
    default JPostDTO entityToDTO(JPost JPost) {
        if (JPost == null) return null;

        return JPostDTO.builder()
                .postId(JPost.getPostId())
                .title(JPost.getTitle())
                .boardCategory(JPost.getBoardCategory())
                .content(JPost.getContent())
                .fileUrl(JPost.getFileUrl())
                .likeCount(JPost.getLikeCount())
                .createdAt(JPost.getCreatedAt())
                .isDeleted(JPost.getIsDeleted())
                // 연관 엔티티는 user만 ID로 노출
                .userId(JPost.getJUser() != null ? JPost.getJUser().getUserId() : null)
                // Enum으로 대체했다면 여기에 .boardType(post.getBoardType()) 등 추가
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
                .createdAt(dto.getCreatedAt())
                .isDeleted(dto.getIsDeleted());

        if (dto.getUserId() != null) {
            builder.JUser(JUser.builder().userId(dto.getUserId()).build());
        }

        return builder.build();
    }
}