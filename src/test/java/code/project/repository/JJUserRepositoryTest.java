package code.project.repository;

import code.project.domain.JMemberRole;
import code.project.domain.JUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class JJUserRepositoryTest {

    @Autowired
    JUserRepository JUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("User 저장/조회")
    void saveAndFindUser() {
        JUser u = JUser.builder()
                .username("tester")
                .password("1234")
                .name("테스터")
                .email("tester@example.com")
                .socialType("LOCAL")
                .build();

        JUser saved = JUserRepository.save(u);

        assertThat(saved.getUserId()).isNotNull();
        assertThat(JUserRepository.existsByUsername("tester")).isTrue();
    }

//    관리자 등급 1명 가입
    @Test
    void 가입() {
        JUser JUser = JUser.builder()
                .username("test2")
                .password(passwordEncoder.encode("1234"))
                .name("테스터2")
                .email("test2@aaa.com")
                .socialType("LOCAL")
                .build();

        JUser.addRole(JMemberRole.USER);

        JUserRepository.save(JUser);
    }

//    사용자 읽어오기
    @Test
    public void 사용자조회(){
        String username = "test4";

        JUser JUser = JUserRepository.getwithRoles(username);
    }

//    회원 삭제
    @Test
    void 회원삭제() {
        String username = "test4";

        JUser JUser = JUserRepository.getwithRoles(username);
        if(JUser == null) {
            return;
        }

        JUserRepository.delete(JUser);
    }
}
