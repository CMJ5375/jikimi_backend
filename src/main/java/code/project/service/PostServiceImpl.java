package code.project.service;

import code.project.domain.Post;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.PostDTO;
import code.project.repository.PostRepository;
import code.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository; // ← User만 유지

    // 조회
    @Override
    @Transactional(readOnly = true)
    public PostDTO get(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + postId));
        return entityToDTO(post);
    }

    // 등록
    @Override
    public Long register(PostDTO dto) {
        Post post = dtoToEntity(dto);

        // User 연관만 세팅 (프록시)
        if (dto.getUserId() != null) {
            post.setUser(userRepository.getReferenceById(dto.getUserId()));
        }

        Post saved = postRepository.save(post);
        return saved.getPostId();
    }

    // 수정
    @Override
    public void modify(PostDTO dto) {
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + dto.getPostId()));

        // 변경 가능한 필드만 반영 (null 은 무시)
        if (dto.getTitle() != null) post.setTitle(dto.getTitle());
        if (dto.getContent() != null) post.setContent(dto.getContent());
        if (dto.getFileUrl() != null) post.setFileUrl(dto.getFileUrl());
        if (dto.getLikeCount() != null) post.setLikeCount(dto.getLikeCount());
        if (dto.getIsDeleted() != null) post.setIsDeleted(dto.getIsDeleted());

        // User 변경이 필요한 경우에만
        if (dto.getUserId() != null) {
            post.setUser(userRepository.getReferenceById(dto.getUserId()));
        }

        // @Transactional 더티 체킹으로 반영되지만 명시 save도 OK
        postRepository.save(post);
    }

    // 삭제 (하드 삭제)
    @Override
    public void remove(Long postId) {
        postRepository.deleteById(postId);

        // 소프트 삭제로 바꾸려면:
        // Post post = postRepository.findById(postId).orElseThrow();
        // post.setIsDeleted(true);
    }

    // 목록(페이징)
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<PostDTO> getList(PageRequestDTO req) {
        Pageable pageable = PageRequest.of(
                Math.max(req.getPage() - 1, 0),
                req.getSize(),
                Sort.by(Sort.Direction.DESC, "postId")
        );

        // 커스텀 검색이 있으면 postRepository.search(req)로 교체
        Page<Post> page = postRepository.findAll(pageable);

        List<PostDTO> dtoList = page.getContent()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<PostDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(req)
                .totalCount(page.getTotalElements())
                .build();
    }
}