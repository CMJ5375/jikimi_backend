package code.project.repository;

import code.project.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select u from User u where u.username = :username")
    User getwithRoles(@Param("username") String username);
}
