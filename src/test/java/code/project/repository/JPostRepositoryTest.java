package code.project.repository;

import code.project.domain.BoardCategory;
import code.project.domain.JPost;
import code.project.domain.JUser;
import code.project.dto.PageRequestDTO;
import code.project.service.JPostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
class JPostRepositoryTest {

    @Autowired
    JPostRepository JPostRepository;

    @Autowired
    JUserRepository JUserRepository;
    @Autowired
    private JPostService JPostService;

    @Test
    void 게시글등록() {
        JUser user = JUserRepository.findById(1L).orElseThrow();
        JPost post = JPost.builder()
                .boardCategory(BoardCategory.FREE)
                .user(user)
                .title("복강경 수술 후기")
                .content("너무 아팠어요 다신 경험하고 싶지 않아요")
                .createdAt(LocalDateTime.now())
                .build();

        JPostRepository.save(post);
    }

    @Test
    void 게시글등록2() {
        JUser user = JUserRepository.findById(1L).orElseThrow();
        JPost post = JPost.builder()
                .boardCategory(BoardCategory.FREE)
                .user(user)
                .title("복강경 수술 후기1")
                .content("너무 아팠어요 다신 경험하고 싶지 않아요1")
                .createdAt(LocalDateTime.now())
                .build();

        JPostRepository.save(post);
    }

    @Test
    void 게시글등록30개() {

        JUser user = JUserRepository.findById(1L).orElseThrow();
        List<JPost> posts = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            JPost post = JPost.builder()
                    .boardCategory(BoardCategory.FREE)
                    .user(user)
                    .title("복강경 수술 후기 " + i)
                    .content("너무 아팠어요... 다신 경험하고 싶지 않아요 " + i)
                    .createdAt(LocalDateTime.now().minusDays(30 - i)) // 날짜를 조금씩 다르게
                    .build();
            posts.add(post);
        }

        JPostRepository.saveAll(posts);
    }

    @Test
    public void 페이징리스트() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
//        log.info("페이징리스트 기본값 {}", postService.getList(pageRequestDTO));
    }
}