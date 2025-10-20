package code.project.repository;

import code.project.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Commit
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("User 저장/조회")
    void saveAndFindUser() {
        User u = User.builder()
                .username("tester")
                .password("1234")
                .name("테스터")
                .email("tester@example.com")
                .role("USER")
                .socialType("LOCAL")
                .build();

        User saved = userRepository.save(u);

        assertThat(saved.getUserId()).isNotNull();
        assertThat(userRepository.existsByUsername("tester")).isTrue();
    }

//    관리자 등급 1명 가입
    @Test
    void 관리자가입() {
        User u = User.builder()
                .username("testAdmin2")
                .password("1234")
                .name("어드민2")
                .email("test2@aaa.com")
                .role("ADMIN")
                .socialType("LOCAL")
                .build();

        User save = userRepository.save(u);
        assertThat(save.getUserId()).isNotNull();
        assertThat(userRepository.existsByUsername("testAdmin2")).isTrue();
    }

//    사용자 읽어오기
    @Test
    public void 사용자조회(){
        String username = "testAdmin2";

        User user = userRepository.getwithRoles(username);
        log.info("조회 {}", user);
        log.info("admin 회원의 권한 {}", user.getMemberRoleList());
    }
}
