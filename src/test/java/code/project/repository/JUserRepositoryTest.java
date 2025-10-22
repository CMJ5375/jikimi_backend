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
class JUserRepositoryTest {

    @Autowired
    private JUserRepository jUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("User 저장 및 조회 테스트")
    void saveAndFindUser() {
        // given
        JUser user = JUser.builder()
                .username("tester")
                .password(passwordEncoder.encode("1234"))
                .name("테스터")
                .email("tester@example.com")
                .socialType("LOCAL")
                .build();

        // when
        JUser saved = jUserRepository.save(user);

        assertThat(saved.getUserId()).isNotNull();
        assertThat(jUserRepository.existsByUsername("tester")).isTrue();

        log.info("저장된 사용자: {}", saved);
    }

    @Test
    @DisplayName("관리자 등급 사용자 가입 테스트")
    void createAdminUser() {
        JUser admin = JUser.builder()
                .username("test2")
                .password(passwordEncoder.encode("1234"))
                .name("테스터2")
                .email("test2@aaa.com")
                .socialType("LOCAL")
                .build();

        // when
        admin.addRole(JMemberRole.USER);
        jUserRepository.save(admin);

        // then
        assertThat(jUserRepository.existsByUsername("test2")).isTrue();
        log.info("관리자 계정 저장 완료: {}", admin);
    }

    @Test
    @DisplayName("사용자 조회 테스트 (권한 포함)")
    void findUserWithRoles() {
        String username = "test4";
        JUser user = jUserRepository.getwithRoles(username);

        if (user != null) {
            log.info("조회된 사용자: {}", user);
            log.info("권한 목록: {}", user.getJMemberRoleList());
        } else {
            log.warn("⚠️ '{}' 사용자 없음", username);
        }
        assertThat(user).isNotNull();
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void deleteUser() {
        String username = "test4";
        JUser user = jUserRepository.getwithRoles(username);

        if (user == null) {
            log.warn("'{}' 사용자 없음 - 삭제 스킵", username);
            return;
        }

        jUserRepository.delete(user);
        assertThat(jUserRepository.existsByUsername(username)).isFalse();

        log.info("'{}' 사용자 삭제 완료", username);
    }
}
