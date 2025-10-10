package code.project.repository;

import code.project.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("1234");
        user.setEmail("test@test.com");
        userRepository.save(user);
    }
}