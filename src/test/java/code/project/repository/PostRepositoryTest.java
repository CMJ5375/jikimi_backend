package code.project.repository;

import code.project.domain.BoardCategory;
import code.project.domain.Post;
import code.project.domain.User;
import code.project.dto.PageRequestDTO;
import code.project.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private PostService postService;

    @Test
    void 게시글등록(){
        User user = userRepository.findById(1L).orElseThrow();
        Post post = Post.builder()
                .boardCategory(BoardCategory.FREE)
                .user(user)
                .title("복강경 수술 후기")
                .content("너무 아팠어요 다신 경험하고 싶지 않아요")
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }

    @Test
    void 게시글등록2(){
        User user = userRepository.findById(1L).orElseThrow();
        Post post = Post.builder()
                .boardCategory(BoardCategory.FREE)
                .user(user)
                .title("복강경 수술 후기1")
                .content("너무 아팠어요 다신 경험하고 싶지 않아요1")
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
    }

    @Test
    public void 페이징리스트() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
        log.info("페이징리스트 기본값 {}", postService.getList(pageRequestDTO));
    }
}