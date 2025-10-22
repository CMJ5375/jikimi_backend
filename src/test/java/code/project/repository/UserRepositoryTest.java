package code.project.repository;

import code.project.domain.MemberRole;
import code.project.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Commit
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("User 저장/조회")
    void saveAndFindUser() {
        User u = User.builder()
                .username("tester")
                .password("1234")
                .name("테스터")
                .email("tester@example.com")
                .socialType("LOCAL")
                .build();

        User saved = userRepository.save(u);

        assertThat(saved.getUserId()).isNotNull();
        assertThat(userRepository.existsByUsername("tester")).isTrue();
    }

//    관리자 등급 1명 가입
    @Test
    void 가입() {
        User user = User.builder()
                .username("test3")
                .password(passwordEncoder.encode("1234"))
                .name("테스터3")
                .email("test3@aaa.com")
                .socialType("LOCAL")
                .build();

        user.addRole(MemberRole.ADMIN);

        userRepository.save(user);
    }

//    사용자 읽어오기
    @Test
    public void 사용자조회(){
        String username = "test4";

        User user = userRepository.getwithRoles(username);
    }

//    회원 삭제
    @Test
    void 회원삭제() {
        String username = "test4";

        User user = userRepository.getwithRoles(username);
        if(user == null) {
            return;
        }

        userRepository.delete(user);
    }
}
