package code.project.service;

import code.project.domain.JUser;
import code.project.dto.JUserDTO;
import code.project.dto.JUserModifyDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Transactional
public interface JUserService {
    // 카카오 회원가입 로직
    JUserDTO getKakaoUser(String accessToken);

    //회원가입 로직
    String register(JUserDTO jUserDTO);

    JUserDTO login(String username, String rawPassword);

    JUserDTO authenticate(String username, String rawPassword);
    void modifyUser(JUserModifyDTO jUserModifyDTO);

    default JUserDTO entityToDTO(JUser JUser) {

        JUserDTO dto = new JUserDTO(
                JUser.getUsername(),
                JUser.getPassword(),
                JUser.getName(),
                JUser.getAddress(),
                JUser.getAge(),
                JUser.getEmail(),
                JUser.getProfileImage(),
                JUser.getJMemberRoleList().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );

        return dto;
    }

    //프로필 사진
    JUserDTO updateProfile(String username, String name, String address, Integer age, MultipartFile image);


}
