package code.project.service;

import code.project.domain.BoardCategory;
import code.project.domain.JUser;
import code.project.dto.JPostDTO;
import code.project.repository.JUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
class JPostServiceTest {

    @Autowired
    JPostService JPostService;

    @Autowired
    JUserRepository JUserRepository;

    @Test
    public void 포스트등록(){
        JUser JUser = JUserRepository.findById(1L).orElseThrow();
        JPostDTO build = JPostDTO.builder()
                .title("title추가")
                .content("내용추가")
                .boardCategory(BoardCategory.FREE)
                .userId(JUser.getUserId())
                .createdAt(LocalDateTime.now())
                .build();

        JPostService.register(build);
    }

    @Test
    public void 수정(){
        JUser JUser = JUserRepository.findById(1L).orElseThrow();
        Long postid = 1L;
        JPostDTO before = JPostService.get(postid);

        before.setTitle("복강경 수술 후기 수정");
        before.setContent("너무 아프지 않았어요");
        before.setCreatedAt(LocalDateTime.now());
        before.setBoardCategory(BoardCategory.HOSPITAL_INFO);

        JPostService.modify(before);
    }
}