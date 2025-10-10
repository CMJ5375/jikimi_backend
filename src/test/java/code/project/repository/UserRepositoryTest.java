package code.project.repository;

import code.project.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

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
}
