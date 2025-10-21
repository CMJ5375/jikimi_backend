package code.project.service;

import code.project.domain.BoardCategory;
import code.project.domain.User;
import code.project.dto.PostDTO;
import code.project.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    UserRepository userRepository;

    @Test
    public void 포스트등록(){
        User user = userRepository.findById(1L).orElseThrow();
        PostDTO build = PostDTO.builder()
                .title("title추가")
                .content("내용추가")
                .boardCategory(BoardCategory.FREE)
                .userId(user.getUserId())
                .createdAt(LocalDateTime.now())
                .build();

        postService.register(build);
    }

    @Test
    public void 수정(){
        User user = userRepository.findById(1L).orElseThrow();
        Long postid = 1L;
        PostDTO before = postService.get(postid);

        before.setTitle("복강경 수술 후기 수정");
        before.setContent("너무 아프지 않았어요");
        before.setCreatedAt(LocalDateTime.now());
        before.setBoardCategory(BoardCategory.HOSPITAL_INFO);

        postService.modify(before);
    }
}