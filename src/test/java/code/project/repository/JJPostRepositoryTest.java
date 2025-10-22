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

@SpringBootTest
@Slf4j
class JJPostRepositoryTest {

    @Autowired
    JPostRepository JPostRepository;

    @Autowired
    JUserRepository JUserRepository;
    @Autowired
    private JPostService JPostService;

    @Test
    void 게시글등록(){
        JUser JUser = JUserRepository.findById(1L).orElseThrow();
        JPost JPost = JPost.builder()
                .boardCategory(BoardCategory.FREE)
                .JUser(JUser)
                .title("복강경 수술 후기")
                .content("너무 아팠어요 다신 경험하고 싶지 않아요")
                .createdAt(LocalDateTime.now())
                .build();

        JPostRepository.save(JPost);
    }

    @Test
    void 게시글등록2(){
        JUser JUser = JUserRepository.findById(1L).orElseThrow();
        JPost JPost = JPost.builder()
                .boardCategory(BoardCategory.FREE)
                .JUser(JUser)
                .title("복강경 수술 후기1")
                .content("너무 아팠어요 다신 경험하고 싶지 않아요1")
                .createdAt(LocalDateTime.now())
                .build();

        JPostRepository.save(JPost);
    }

    @Test
    public void 페이징리스트() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
//        log.info("페이징리스트 기본값 {}", postService.getList(pageRequestDTO));
    }
}