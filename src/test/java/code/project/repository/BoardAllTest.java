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
    @Autowired BoardRepository boardRepository;
    @Autowired BoardCategoryRepository categoryRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired NoticeRepository noticeRepository;

    @Test
    @DisplayName("게시판-카테고리-게시글-댓글 CRUD 연결 검증")
    void boardPostCommentFlow() {
        // 유저 생성
        User user = userRepository.save(User.builder()
                .username("writer")
                .password("1234")
                .name("글쓴이")
                .email("writer@example.com")
                .socialType("LOCAL")
                .build());
        user.addRole(MemberRole.USER);

        // 게시판 & 카테고리
        Board board = boardRepository.save(Board.builder()
                .name("자료실")
                .build());

        BoardCategory cat = categoryRepository.save(BoardCategory.builder()
                .board(board)
                .name("병원정보")
                .description("병원 데이터")
                .build());

        // 게시글
        Post post = postRepository.save(Post.builder()
                .board(board)
                .category(cat)      // null 가능
                .user(user)
                .title("서울 병원 목록")
                .content("내용입니다")
                .build());

        // 댓글
        Comment c1 = commentRepository.save(Comment.builder()
                .post(post)
                .user(user)
                .content("좋은 정보 감사합니다!")
                .build());

        List<Post> boardPosts = postRepository.findByBoard_BoardId(board.getBoardId());
        List<Comment> postComments = commentRepository.findByPost_PostId(post.getPostId());

        assertThat(boardPosts).hasSize(1);
        assertThat(postComments).hasSize(1);
        assertThat(postComments.get(0).getContent()).contains("감사");
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
                .build());
        admin.addRole(MemberRole.ADMIN);

        Notice notice = noticeRepository.save(Notice.builder()
                .user(admin) // 실제 앱에서는 Service에서 role='ADMIN' 검증
                .title("점검 안내")
                .content("10/10 02:00~03:00 서버 점검")
                .build());

        assertThat(notice.getNoticeId()).isNotNull();
        assertThat(notice.getUser().getMemberRoleList()).isEqualTo("ADMIN");
    }
}
