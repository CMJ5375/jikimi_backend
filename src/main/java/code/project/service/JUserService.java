package code.project.service;

import code.project.domain.JUser;
import code.project.dto.JUserDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Transactional
public interface JUserService {

    JUserDTO getKakaoUser(String accessToken);

    default JUserDTO entityToDTO(JUser JUser) {

        JUserDTO dto = new JUserDTO(
                JUser.getUsername(),
                JUser.getPassword(),
                JUser.getName(),
                JUser.getAddress(),
                JUser.getAge(),
                JUser.getEmail(),
                JUser.getJMemberRoleList().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );

        return dto;
    }
}
