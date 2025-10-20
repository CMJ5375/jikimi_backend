package code.project.repository;

import code.project.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Commit
class BoardAllTest {

    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired NoticeRepository noticeRepository;

    @Test
    @DisplayName("게시판-카테고리-게시글-댓글 CRUD 연결 검증")
    void boardPostCommentFlow() {
        // 유저 생성

    }

    @Test
    @DisplayName("공지사항은 ADMIN만 작성 (서비스 레이어에서 롤 체크 가정)")
    void noticeByAdmin() {
        User admin = userRepository.save(User.builder()
                .username("admin")
                .password("admin")
                .name("관리자")
                .email("admin@example.com")
                .socialType("LOCAL")
                .role("ADMIN")
                .build());

        Notice notice = noticeRepository.save(Notice.builder()
                .user(admin) // 실제 앱에서는 Service에서 role='ADMIN' 검증
                .title("점검 안내")
                .content("10/10 02:00~03:00 서버 점검")
                .build());

        assertThat(notice.getNoticeId()).isNotNull();
        assertThat(notice.getUser().getRole()).isEqualTo("ADMIN");
    }
}
