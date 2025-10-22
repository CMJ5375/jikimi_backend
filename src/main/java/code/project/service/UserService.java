package code.project.service;

import code.project.domain.User;
import code.project.dto.UserDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Transactional
public interface UserService {

    UserDTO getKakaoUser(String accessToken);

    default UserDTO entityToDTO(User user) {

        UserDTO dto = new UserDTO(
                user.getUsername(),
                user.getPassword(),
                user.getName(),
                user.getAddress(),
                user.getAge(),
                user.getEmail(),
                user.getMemberRoleList().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );

        return dto;
    }
}
