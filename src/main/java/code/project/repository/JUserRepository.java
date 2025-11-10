package code.project.repository;

import code.project.domain.JUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JUserRepository extends JpaRepository<JUser, Long> {
    // 이미 존재하는 회원인지 확인
    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"JMemberRoleList"})
    @Query("select u from JUser u where u.username = :username")
    JUser getwithRoles(@Param("username") String username);

    Optional<JUser> getCodeUserByUsername(String username);

    Optional<JUser> findByUsername(String username);

    @Query("select u.userId from JUser u where u.username = :username")
    Optional<Long> findIdByUsername(@Param("username") String username);
}
