// src/main/java/code/project/controller/JPostController.java
package code.project.controller;

import code.project.domain.BoardCategory;
import code.project.domain.JPost;
import code.project.domain.JUser;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.JPostDTO;
import code.project.repository.JPostLikeRepository;
import code.project.repository.JPostRepository;
import code.project.repository.JUserRepository;
import code.project.service.JPostService;
import code.project.util.CustomS3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class JPostController {

    private final JPostService jPostService;
    private final JPostRepository jPostRepository;
    private final JUserRepository jUserRepository;
    private final JPostLikeRepository jPostLikeRepository;
    private final CustomS3Util s3Util;

    /** 상단 고정 인기글 */
    @GetMapping("/hot/pins")
    public ResponseEntity<List<JPostDTO>> getHotPins() {
        // 리포지토리에 "좋아요 3개 이상" 상위 3개 메서드가 있는 전제
        // findTop3ByLikeCountGreaterThanEqualAndIsDeletedFalseOrderByLikeCountDescPostIdDesc(3)
        List<JPostDTO> pins = jPostService.getHotPins();
        return ResponseEntity.ok(pins);
    }

    /** 목록 (페이징) */
    @GetMapping("/list")
    public PageResponseDTO<JPostDTO> list(
            PageRequestDTO req,
            @RequestParam(required = false, defaultValue = "DEFAULT") String sort,
            @RequestParam(required = false, defaultValue = "7") Integer days
    ) {
        // 🔒 파라미터 정규화: 빈 문자열 → null
        String q = (req.getQ() != null && !req.getQ().isBlank()) ? req.getQ() : null;

        String catRaw = req.getBoardCategory();
        BoardCategory category = null;
        if (catRaw != null && !catRaw.isBlank()) {
            try {
                category = BoardCategory.valueOf(catRaw);
            } catch (IllegalArgumentException ignore) {
                // 잘못된 카테고리 값은 무시(전체 검색)
                category = null;
            }
        }

        String sortNorm = (sort == null) ? "DEFAULT" : sort.trim().toUpperCase(Locale.ROOT);
        int page = Math.max(1, req.getPage());
        int size = Math.max(1, req.getSize());
        int daysVal = (days == null ? 7 : Math.max(1, days));

        // PageRequestDTO에 정규화 값 되돌려 세팅(서비스에서 그대로 사용)
        req.setQ(q);
        req.setBoardCategory(category == null ? null : category.name());
        req.setSort(sortNorm);
        req.setDays(String.valueOf(daysVal));
        req.setPage(page);
        req.setSize(size);

        return jPostService.getList(req);
    }

    /** 단건 조회(조회수 증가 포함) */
    @GetMapping("/{postId}")
    public JPostDTO get(@PathVariable Long postId) {
        jPostService.incrementView(postId);
        return jPostService.get(postId);
    }

    /** 등록: Multipart + S3 업로드 */
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long register(
            @RequestPart("post") JPostDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        log.info(">> /api/posts/add called, authorUsername={}, title={}, category={}, hasFile={}",
                dto.getAuthorUsername(), dto.getTitle(), dto.getBoardCategory(), file != null && !file.isEmpty());

        // 1) 작성자(username) 확인
        JUser user = jUserRepository.findByUsername(dto.getAuthorUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + dto.getAuthorUsername()));

        // 2) 필수 값 가드
        if (dto.getBoardCategory() == null) {
            throw new IllegalArgumentException("boardCategory is required");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new IllegalArgumentException("content is required");
        }

        // 3) 선저장으로 postId 확보
        JPost entity = JPost.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .boardCategory(dto.getBoardCategory())
                .fileUrl(null)
                .likeCount(0)
                .viewCount(0)
                .isDeleted(false)
                .user(user)
                .build();

        JPost saved = jPostRepository.save(entity);
        Long postId = saved.getPostId();
        log.info(">> saved postId = {}", postId);

        // 4) 파일 있으면 S3 업로드 → 절대 URL을 fileUrl로 저장
        if (file != null && !file.isEmpty()) {
            String rawName = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
            String safeName = new java.io.File(rawName).getName().replace("/", "_");
            String key = "posts/" + postId + "/" + safeName;

            s3Util.uploadBytes(file.getBytes(), key, file.getContentType());

            String objectUrl = s3Util.objectUrl(key);
            saved.setFileUrl(objectUrl);
            jPostRepository.save(saved);

            log.info(">> file uploaded to S3 key={}, url={}", key, objectUrl);
        } else {
            log.info(">> no file uploaded for this post");
        }

        return postId;
    }

    /** 수정(작성자만) */
    @PutMapping("/{postId}")
    public ResponseEntity<?> modify(
            @PathVariable Long postId,
            @RequestBody JPostDTO dto,
            Authentication auth
    ) {
        if (auth == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        String loginUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
        try {
            jPostService.modifyOwned(postId, loginUsername, isAdmin, dto);
            return ResponseEntity.ok().build();
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(se.getMessage());
        }
    }

    /** 삭제(작성자 또는 관리자) */
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> remove(
            @PathVariable Long postId,
            Authentication auth
    ) {
        if (auth == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        String loginUsername = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        try {
            jPostService.removeWithAuth(postId, loginUsername, isAdmin);
            return ResponseEntity.ok().build();
        } catch (SecurityException se) {
            return ResponseEntity.status(403).body(se.getMessage());
        }
    }

    /** 조회수 증가 (별도 호출용) */
    @PatchMapping("/{id}/views")
    public ResponseEntity<Map<String, Object>> incrementViews(@PathVariable Long id) {
        jPostService.incrementView(id);
        int updated = jPostRepository.findById(id)
                .map(JPost::getViewCount)
                .orElse(0);
        return ResponseEntity.ok(Map.of("viewCount", updated));
    }

    /** 좋아요 토글 */
    @PatchMapping("/{id}/likes")
    public ResponseEntity<Map<String, Object>> incrementLikes(
            @PathVariable("id") Long postId,
            @RequestParam("username") String username
    ) {
        jPostService.incrementLike(postId, username);

        int updatedLikeCount = jPostRepository.findById(postId)
                .map(JPost::getLikeCount)
                .orElse(0);

        boolean liked = jPostLikeRepository.findByPostAndUser(
                jPostRepository.getReferenceById(postId),
                jUserRepository.findByUsername(username).orElseThrow()
        ).isPresent();

        return ResponseEntity.ok(
                Map.of("likeCount", updatedLikeCount, "liked", liked)
        );
    }

    /** 좋아요 상태 조회 */
    @GetMapping("/{id}/likes/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable Long id,
            @RequestParam String username
    ) {
        boolean liked = jPostService.isUserLiked(id, username);
        int likeCount = jPostRepository.findById(id)
                .map(JPost::getLikeCount)
                .orElse(0);

        return ResponseEntity.ok(Map.of("liked", liked, "likeCount", likeCount));
    }

    /** 내 글 목록 */
    @GetMapping("/my")
    public ResponseEntity<?> getMyPosts(Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        String loginUsername = auth.getName();
        List<JPostDTO> list = jPostService.getMyPosts(loginUsername);
        return ResponseEntity.ok(list);
    }
}
