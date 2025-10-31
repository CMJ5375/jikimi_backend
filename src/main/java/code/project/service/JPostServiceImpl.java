package code.project.service;

import code.project.domain.JPost;
import code.project.domain.BoardCategory;
import code.project.domain.JPostLike;
import code.project.domain.JUser;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.JPostDTO;
import code.project.repository.JCommentRepository;
import code.project.repository.JPostLikeRepository;
import code.project.repository.JPostRepository;
import code.project.repository.JUserRepository;
import code.project.service.hotpin.HotPinManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JPostServiceImpl implements JPostService {

    private final JPostRepository jPostRepository;
    private final JUserRepository jUserRepository; // ← User만 유지
    private final JPostLikeRepository jPostLikeRepository;
    private final JCommentRepository jCommentRepository;

    // 메모리 인기글 큐 주입
    private final HotPinManager hotPinManager;
    // 좋아요 3개 이상이면 인기글로
    private static final int HOT_THRESHOLD = 3;

    // 조회
    @Override
    @Transactional(readOnly = true)
    public JPostDTO get(Long postId) {
        JPost JPost = jPostRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + postId));
        return entityToDTO(JPost);
    }

    // 등록 커밋/브렌치 테스트
    @Override
    public Long register(JPostDTO dto) {

        // username으로 유저 찾기
        JUser user = jUserRepository.findByUsername(dto.getAuthorUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + dto.getAuthorUsername()));

        JPost entity = JPost.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .boardCategory(dto.getBoardCategory())
                .fileUrl(dto.getFileUrl())
                .likeCount(0)
                .viewCount(0)
                .isDeleted(false)
                .user(user)
                .build();

        JPost saved = jPostRepository.save(entity);
        return saved.getPostId();
    }

    //새 수정(글쓴이만 삭제.수정/관리자만 삭제가능)
    @Override
    public void modifyOwned(Long postId,
                            String loginUsername,
                            boolean isAdmin,
                            JPostDTO dto) {

        // 글 찾기
        JPost post = jPostRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + postId));

        // 글쓴이 username
        String ownerUsername = post.getUser().getUsername();

        // 수정은 "작성자 본인만" 가능. (관리자라도 본인 글 아니면 수정 금지)
        if (!ownerUsername.equals(loginUsername)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        // 수정 가능한 필드 반영 (null이면 무시)
        if (dto.getTitle() != null) {
            post.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            post.setContent(dto.getContent());
        }
        if (dto.getFileUrl() != null) {
            post.setFileUrl(dto.getFileUrl());
        }
        // likeCount, isDeleted 같은 건 일반 수정 화면에서 바꿀 일 없으면 안 건드려도 돼
        // post.setLikeCount(...) 이런 건 빼도 됨

        // 변경 저장
        jPostRepository.save(post);
    }

    //새 삭제(글쓴이만 삭제.수정/관리자만 삭제가능)
    @Override
    public void removeWithAuth(Long postId,
                               String loginUsername,
                               boolean isAdmin) {

        JPost post = jPostRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + postId));

        String ownerUsername = post.getUser().getUsername();

        // 삭제는 두 경우 허용:
        // 1) 내가 쓴 글이면 삭제 가능
        // 2) 관리자는 누구 글이든 삭제 가능
//        if (!(ownerUsername.equals(loginUsername) || isAdmin)) {
//            throw new SecurityException("삭제 권한이 없습니다.");
//        }

        // 하드 삭제
        jPostRepository.delete(post);

        // 만약 소프트 삭제 쓰고 싶으면 위 줄 대신:
        // post.setIsDeleted(true);
        // JPostRepository.save(post);
    }

    //조회
    @Override
    public void incrementView(Long id) {
        System.out.println(">>> incrementView called for post " + id);
        int updated = jPostRepository.incrementView(id);
        if (updated == 0) {
            throw new EntityNotFoundException("Post not found id=" + id);
        }
    }

    // 게시글 좋아요
    @Override
    public void incrementLike(Long postId, String username) {

        // 1. 게시글 찾기
        JPost post = jPostRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + postId));

        // 2. 유저 찾기
        JUser user = jUserRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        // 3. 이미 좋아요 했는지 확인
        var optLike = jPostLikeRepository.findByPostAndUser(post, user);

        if (optLike.isPresent()) {
            // ==== 이미 눌렀던 경우 -> 취소 ====

            // (1) post_like 행 삭제
            jPostLikeRepository.deleteByPostAndUser(post, user);

            // (2) likeCount -1 (0 밑으로는 안내려가게)
            int current = (post.getLikeCount() == null ? 0 : post.getLikeCount());
            post.setLikeCount(Math.max(current - 1, 0));

            jPostRepository.save(post);

            // 좋아요가 3 미만이면 핀에서 제거
            if (post.getLikeCount() < HOT_THRESHOLD) {
                hotPinManager.removePin(post.getPostId());
            }

        } else {
            // ==== 처음 누르는 경우 -> 추가 ====

            JPostLike like = JPostLike.builder()
                    .post(post)
                    .user(user)
                    .build();

            jPostLikeRepository.save(like);

            int current = (post.getLikeCount() == null ? 0 : post.getLikeCount());
            post.setLikeCount(current + 1);
            jPostRepository.save(post);

            // 좋아요가 3 이상이면 인기글 큐에 등록
            if (post.getLikeCount() >= HOT_THRESHOLD) {
                hotPinManager.addPinIfAbsent(post.getPostId());
            }
        }
    }

    // 게시글 좋아요한 유저 찾기
    @Override
    @Transactional(readOnly = true)
    public boolean isUserLiked(Long postId, String username) {

        // 게시글 엔티티 찾기
        JPost post = jPostRepository.findById(postId)
                .orElseThrow(() ->
                        new NoSuchElementException("Post not found: " + postId));

        // 사용자 엔티티 찾기 (username으로!)
        JUser user = jUserRepository.findByUsername(username)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + username));

        // post + user 조합으로 좋아요 테이블에 row 있는지 확인
        return jPostLikeRepository.findByPostAndUser(post, user).isPresent();
    }

    // JPostServiceImpl
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<JPostDTO> getList(PageRequestDTO req) {
        boolean popular = "POPULAR".equalsIgnoreCase(req.getSort());

        log.info("REQ sort={}, days={}", req.getSort(), req.getDays());

        Pageable pageable = popular
                ? PageRequest.of(Math.max(req.getPage()-1,0), req.getSize(),
                Sort.by(Sort.Order.desc("likeCount"),
                        Sort.Order.desc("viewCount"),
                        Sort.Order.desc("createdAt")))
                : PageRequest.of(Math.max(req.getPage()-1,0), req.getSize(),
                Sort.by(Sort.Direction.DESC, "postId"));

        // 안전 파싱 (못 파싱하면 null)
        BoardCategory category = null;
        if (req.getBoardCategory()!=null && !req.getBoardCategory().isBlank()) {
            try { category = BoardCategory.valueOf(req.getBoardCategory().toUpperCase()); }
            catch (Exception ignore) {}
        }
        String q = (req.getQ()!=null && !req.getQ().isBlank()) ? req.getQ() : null;

        Page<JPost> page;

        if (popular) {
            int days = 7;
            try {
                if (req.getDays() != null && !req.getDays().isBlank()) {
                    days = Math.max(Integer.parseInt(req.getDays()), 1);
                }
            } catch (NumberFormatException ignore) {
                days = 7;
            }

            LocalDateTime since = LocalDate.now()
                    .minusDays(days)
                    .atStartOfDay();
            //log.info("POPULAR mode. days={}, since={}", days, since);

            page = jPostRepository.findPopular(category, q, since, pageable);
        } else {
            page = jPostRepository.findDefault(category, q, pageable);
        }

        //log.info("RESULT total={}, pageSize={}", page.getTotalElements(), page.getNumberOfElements());

        var dtoList = page.getContent().stream().map(this::entityToDTO).toList();
        return PageResponseDTO.<JPostDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(req)
                .totalCount(page.getTotalElements())
                .build();
    }

    @Override
    public JPostDTO entityToDTO(JPost p) {
        if (p == null) return null;

        String authorName = "익명";
        if (p.getUser() != null) {
            var u = p.getUser();
            if (u.getName() != null && !u.getName().isBlank()) {
                authorName = u.getName();
            } else if (u.getUsername() != null && !u.getUsername().isBlank()) {
                authorName = u.getUsername();
            } else if (u.getEmail() != null && !u.getEmail().isBlank()) {
                authorName = u.getEmail().split("@")[0];
            }
        }

        // 좋아요 누른 사람 username 목록 만들기
        List<String> likedNames = p.getLikes().stream()
                .map(like -> like.getUser().getUsername())
                .toList();

        return JPostDTO.builder()
                .postId(p.getPostId())
                .title(p.getTitle())
                .content(p.getContent())
                .boardCategory(p.getBoardCategory())
                .fileUrl(p.getFileUrl())
                .likeCount(p.getLikeCount())
                .viewCount(p.getViewCount())
                .createdAt(p.getCreatedAt())
                .isDeleted(p.getIsDeleted())

                .userId(p.getUser() != null ? p.getUser().getUserId() : null)
                .authorName(authorName)
                .authorUsername(p.getUser() != null ? p.getUser().getUsername() : null)
                .likedUsernames(likedNames)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JPostDTO> getMyPosts(String username) {
        var entities = jPostRepository
                .findByUser_UsernameAndIsDeletedFalseOrderByPostIdDesc(username);

        // ✅ 1) postId 모으기
        var ids = entities.stream().map(JPost::getPostId).toList();

        // ✅ 2) 벌크 카운트 실행 → Map<Long, Integer>로 변환
        var countMap = new HashMap<Long, Integer>();
        if (!ids.isEmpty()) {
            for (Object[] row : jCommentRepository.countGroupByPostIds(ids)) {
                Long postId = (Long) row[0];
                Long cnt = (Long) row[1]; // JPA가 Long으로 가져옴
                countMap.put(postId, cnt.intValue());
            }
        }

        // ✅ 3) DTO 매핑 + commentCount 주입
        return entities.stream()
                .map(p -> {
                    var dto = entityToDTO(p);
                    dto.setCommentCount(countMap.getOrDefault(p.getPostId(), 0));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 상단 고정 인기글 조회
    @Override
    @Transactional(readOnly = true)
    public List<JPostDTO> getHotPins() {
        List<Long> ids = hotPinManager.getPinsNewestFirst();
        if (ids.isEmpty()) return List.of();
        Map<Long, JPost> map = new HashMap<>();
        jPostRepository.findAllById(ids).forEach(p -> map.put(p.getPostId(), p));
        List<JPostDTO> list = new ArrayList<>();
        for (Long id : ids) {
            JPost p = map.get(id);
            if (p != null) list.add(entityToDTO(p));
        }
        return list;
    }
}