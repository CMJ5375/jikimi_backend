package code.project.service;

import code.project.domain.JPost;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.JPostDTO;
import code.project.repository.JPostRepository;
import code.project.repository.JUserRepository;
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
public class JPostServiceImpl implements JPostService {

    private final JPostRepository JPostRepository;
    private final JUserRepository JUserRepository; // ← User만 유지

    // 조회
    @Override
    @Transactional(readOnly = true)
    public JPostDTO get(Long postId) {
        JPost JPost = JPostRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + postId));
        return entityToDTO(JPost);
    }

    // 등록
    @Override
    public Long register(JPostDTO dto) {
        JPost JPost = dtoToEntity(dto);

        // User 연관만 세팅 (프록시)
        if (dto.getUserId() != null) {
            JPost.setUser(JUserRepository.getReferenceById(dto.getUserId()));
        }

        JPost saved = JPostRepository.save(JPost);
        return saved.getPostId();
    }

    // 수정
    @Override
    public void modify(JPostDTO dto) {
        JPost JPost = JPostRepository.findById(dto.getPostId())
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + dto.getPostId()));

        // 변경 가능한 필드만 반영 (null 은 무시)
        if (dto.getTitle() != null) JPost.setTitle(dto.getTitle());
        if (dto.getContent() != null) JPost.setContent(dto.getContent());
        if (dto.getFileUrl() != null) JPost.setFileUrl(dto.getFileUrl());
        if (dto.getLikeCount() != null) JPost.setLikeCount(dto.getLikeCount());
        if (dto.getIsDeleted() != null) JPost.setIsDeleted(dto.getIsDeleted());

        // User 변경이 필요한 경우에만
        if (dto.getUserId() != null) {
            JPost.setUser(JUserRepository.getReferenceById(dto.getUserId()));
        }

        // @Transactional 더티 체킹으로 반영되지만 명시 save도 OK
        JPostRepository.save(JPost);
    }

    // 삭제 (하드 삭제)
    @Override
    public void remove(Long postId) {
        JPostRepository.deleteById(postId);

        // 소프트 삭제로 바꾸려면:
        // Post post = postRepository.findById(postId).orElseThrow();
        // post.setIsDeleted(true);
    }

    // 목록(페이징)
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<JPostDTO> getList(PageRequestDTO req) {
        Pageable pageable = PageRequest.of(
                Math.max(req.getPage() - 1, 0),
                req.getSize(),
                Sort.by(Sort.Direction.DESC, "postId")
        );

        // 커스텀 검색이 있으면 postRepository.search(req)로 교체
        Page<JPost> page = JPostRepository.findAll(pageable);

        List<JPostDTO> dtoList = page.getContent()
                .stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<JPostDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(req)
                .totalCount(page.getTotalElements())
                .build();
    }
}