package code.project.controller;

import code.project.domain.JPost;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.JPostDTO;
import code.project.repository.JPostLikeRepository;
import code.project.repository.JPostRepository;
import code.project.repository.JUserRepository;
import code.project.service.JPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
public class JPostController {

    private final JPostService jPostService;
    private final JPostRepository jPostRepository;
    private final JUserRepository jUserRepository;
    private final JPostLikeRepository jPostLikeRepository;


    // 목록 (페이징)
    @GetMapping("/list")
    public PageResponseDTO<JPostDTO> list(PageRequestDTO pageRequestDTO) {
        return jPostService.getList(pageRequestDTO);
    }

    // 단건 조회 => 조회수증가
    @GetMapping("/{postId}")
    public JPostDTO get(@PathVariable Long postId) {
        jPostService.incrementView(postId); // 조회수 1 증가
        return jPostService.get(postId);
    }

    // 등록
    @PostMapping("/add")
    public Long register(@RequestBody JPostDTO dto) {
        return jPostService.register(dto);
    }

    // 수정
    //@PutMapping("/{postId}")
    //public void modify(@PathVariable Long postId, @RequestBody JPostDTO dto) {
    //    dto.setPostId(postId);
    //    jPostService.modify(dto);
    //}

    // 삭제
    //@DeleteMapping("/{postId}")
    //public void remove(@PathVariable Long postId) {
    //    jPostService.remove(postId);
    //}

    //바뀐 수정
    @PutMapping("/{postId}")
    public ResponseEntity<?> modify(
            @PathVariable Long postId,
            @RequestBody JPostDTO dto,
            Authentication auth
    ) {
        log.info("postId : {}", postId);
        // 비로그인 요청 방어
        if (auth == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 현재 로그인한 아이디 (username)
        String loginUsername = auth.getName();

        // 관리자 여부 체크
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

    //바뀐 삭제
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

    //게시판 수정,삭제 포인트 정리:
    //
    //auth == null → 401 (로그인 안 한 사람)
    //SecurityException → 403 (권한 없음)
    //수정할 때는 서비스에서 작성자 본인만 허용
    //삭제할 때는 작성자 or 관리자 허용


     //조회수 증가용 //오류 없으면 삭제
    @PatchMapping("/{id}/views")
    public ResponseEntity<Map<String, Object>> incrementViews(@PathVariable Long id) {
        // 1) 조회수 +1
        jPostService.incrementView(id);

        // 2) 증가된 최신 값 다시 조회해서 클라로 돌려주기
        int updated = jPostRepository.findById(id)
                .map(JPost::getViewCount)
                .orElse(0);

        return ResponseEntity.ok(Map.of("viewCount", updated));
    }

    //좋아요
    @PatchMapping("/{id}/likes")
    public ResponseEntity<Map<String, Object>> incrementLikes(
            @PathVariable("id") Long postId,
            @RequestParam("username") String username
    ) {
        // toggle 처리
        jPostService.incrementLike(postId, username);

        // 최신 likeCount 가져오기
        int updatedLikeCount = jPostRepository.findById(postId)
                .map(JPost::getLikeCount)
                .orElse(0);

        // 지금 사용자가 좋아요중인지 다시 체크해서 보내주기
        boolean liked = jPostLikeRepository.findByPostAndUser(
                jPostRepository.getReferenceById(postId),
                jUserRepository.findByUsername(username).orElseThrow()
        ).isPresent();

        return ResponseEntity.ok(
                Map.of(
                        "likeCount", updatedLikeCount,
                        "liked", liked // 프론트에서 이걸로 파란색 여부 결정
                )
        );
    }

    @GetMapping("/{id}/likes/status")
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable Long id,
            @RequestParam String username
    ) {
        // Service 인스턴스(jPostService)로 호출
        boolean liked = jPostService.isUserLiked(id, username);

        int likeCount = jPostRepository.findById(id)
                .map(JPost::getLikeCount)
                .orElse(0);

        return ResponseEntity.ok(
                Map.of(
                        "liked", liked,
                        "likeCount", likeCount
                )
        );
    }

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