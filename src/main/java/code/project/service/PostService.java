package code.project.service;

import code.project.domain.Post;
import code.project.domain.User;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.PostDTO;

public interface PostService {

    // 단건 조회
    PostDTO get(Long postId);

    // 등록
    Long register(PostDTO dto);

    // 수정
    void modify(PostDTO dto);

    // 삭제(소프트 딜리트라면 isDeleted = true 처리)
    void remove(Long postId);

    // 페이징 목록
    PageResponseDTO<PostDTO> getList(PageRequestDTO pageRequestDTO);

    // ====== Mapper ======

    // Entity -> DTO
    default PostDTO entityToDTO(Post post) {
        if (post == null) return null;

        return PostDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .boardCategory(post.getBoardCategory())
                .content(post.getContent())
                .fileUrl(post.getFileUrl())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .isDeleted(post.getIsDeleted())
                // 연관 엔티티는 user만 ID로 노출
                .userId(post.getUser() != null ? post.getUser().getUserId() : null)
                // Enum으로 대체했다면 여기에 .boardType(post.getBoardType()) 등 추가
                .build();
    }

    // DTO -> Entity
    //  - 연관 엔티티는 ID만 채운 프록시로 세팅(서비스 구현에서 getReferenceById로 치환해도 OK)
    default Post dtoToEntity(PostDTO dto) {
        if (dto == null) return null;

        Post.PostBuilder builder = Post.builder()
                .postId(dto.getPostId())
                .title(dto.getTitle())
                .boardCategory(dto.getBoardCategory())
                .content(dto.getContent())
                .fileUrl(dto.getFileUrl())
                .likeCount(dto.getLikeCount())
                .createdAt(dto.getCreatedAt())
                .isDeleted(dto.getIsDeleted());

        if (dto.getUserId() != null) {
            builder.user(User.builder().userId(dto.getUserId()).build());
        }
        // Enum으로 대체했다면 여기서 builder.boardType(dto.getBoardType()) 등 세팅

        return builder.build();
    }
}