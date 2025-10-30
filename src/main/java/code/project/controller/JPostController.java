package code.project.controller;

import code.project.domain.JPost;
import code.project.domain.JUser;
import code.project.dto.PageRequestDTO;
import code.project.dto.PageResponseDTO;
import code.project.dto.JPostDTO;
import code.project.repository.JPostLikeRepository;
import code.project.repository.JPostRepository;
import code.project.repository.JUserRepository;
import code.project.service.JPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
    public PageResponseDTO<JPostDTO> list(
            PageRequestDTO req,
            @RequestParam(required=false, defaultValue="DEFAULT") String sort,
            @RequestParam(required=false, defaultValue="7") Integer days
    ) {
        // 빈 문자열 -> null 로 정규화
        String cat = (req.getBoardCategory()!=null && !req.getBoardCategory().isBlank())
                ? req.getBoardCategory() : null;
        req.setBoardCategory(cat);
        req.setSort(sort);
        req.setDays(String.valueOf(days == null ? 7 : days));

        return jPostService.getList(req); // ← 서비스는 req 하나만 받도록
    }

    // 단건 조회 => 조회수증가
    @GetMapping("/{postId}")
    public JPostDTO get(@PathVariable Long postId) {
        jPostService.incrementView(postId); // 조회수 1 증가
        return jPostService.get(postId);
    }

    // 새 등록 (파일 업로드 + fileUrl 세팅)
    @PostMapping(
            value = "/add",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Long register(
            @RequestPart("post") JPostDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        // 0. 디버그
        System.out.println(">> /api/posts/add called");
        System.out.println(">> dto.authorUsername = " + dto.getAuthorUsername());
        System.out.println(">> dto.title = " + dto.getTitle());
        System.out.println(">> dto.boardCategory = " + dto.getBoardCategory());
        System.out.println(">> incoming file = " + (file != null ? file.getOriginalFilename() : "null"));

        // 1) 작성자(username)로 유저 찾기
        JUser user = jUserRepository.findByUsername(dto.getAuthorUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + dto.getAuthorUsername()));

        // 2) 먼저 Post 엔티티를 저장해서 postId 확보 (파일 경로 계산에 필요)
        JPost entity = JPost.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .boardCategory(dto.getBoardCategory())
                .fileUrl(null) // 일단 비워두고 나중에 채움
                .likeCount(0)
                .viewCount(0)
                .isDeleted(false)
                .user(user)
                .build();

        JPost saved = jPostRepository.save(entity); // 여기서 postId 생성됨
        Long postId = saved.getPostId();

        System.out.println(">> saved postId = " + postId);

        // 3) 파일이 있으면 디스크에 저장
        if (file != null && !file.isEmpty()) {

            // (3-1) 업로드 루트 경로 결정
            // 프로젝트 루트 기준으로 "uploads/{postId}" 폴더에 저장
            String uploadRootPath = System.getProperty("user.dir")
                    + File.separator + "uploads";

            File dir = new File(uploadRootPath, postId.toString());
            if (!dir.exists()) {
                boolean mk = dir.mkdirs();
                System.out.println(">> mkdirs: " + dir.getAbsolutePath() + " result=" + mk);
            }

            // (3-2) 원본 파일명 안전하게 정제
            String rawName = file.getOriginalFilename();        // 예: "아이 피부질환.png" 또는 "C:\\fakepath\\FGroup.png"
            if (rawName == null) rawName = "file";

            // 윈도우/브라우저가 경로 통째로 줄 수도 있으니까 마지막 조각만 취함
            String safeName = new File(rawName).getName();      // "C:\fakepath\FGroup.png" -> "FGroup.png"

            // 혹시라도 파일명에 슬래시(/)가 포함된 미친 경우 방어 (Tomcat path variable 깨지는 원인)
            safeName = safeName.replace("/", "_");

            System.out.println(">> final safeName = " + safeName);

            // (3-3) 실제 저장할 물리 파일 경로
            File dest = new File(dir, safeName);
            file.transferTo(dest); // 디스크에 저장 완료
            System.out.println(">> file saved to " + dest.getAbsolutePath());

            // (3-4) 이 파일을 내려줄 다운로드 URL을 DB에 넣는다
            // 최종적으로 프론트에서는 이 값을 그대로 받아
            //   http://localhost:8080 + fileUrl
            // 로 열게 할 거야.
            //
            // 모양은 꼭 이렇게: /files/{postId}/{safeName}
            String downloadPath = "/files/" + postId + "/" + safeName;

            // DB에 다시 반영
            saved.setFileUrl(downloadPath);
            jPostRepository.save(saved);

            System.out.println(">> saved.fileUrl = " + saved.getFileUrl());
        } else {
            System.out.println(">> no file uploaded for this post");
        }

        // 4) 최종적으로 postId 반환
        return postId;
    }

    // 등록
    //@PostMapping("/add")
    //public Long register(@RequestBody JPostDTO dto) {
    //    return jPostService.register(dto);
    //}

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