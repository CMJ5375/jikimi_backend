package code.project.security;

import code.project.domain.JUser;
import code.project.dto.JUserDTO;
import code.project.repository.JUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final JUserRepository JUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("시큐리티가 사용자 정보조회, 처리하나?, 사용자이름은? {}", username);
        JUser JUser = JUserRepository.getwithRoles(username);
//        멤버는 entity이고 우리가 반환해야하는 것은 userDTO자료형이다.
        if(JUser == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        JUserDTO JUserDTO = new JUserDTO(
                JUser.getUserId(),
                JUser.getUsername(),
                JUser.getPassword(),
                JUser.getName(),
                JUser.getAddress(),
                JUser.getAge(),
                JUser.getEmail(),
                null,   //profileImage
                JUser.getJMemberRoleList()
                        .stream()
                        .map(memberRole -> memberRole.name())
                        .collect(Collectors.toList())
        );
        log.info("로그인한 멤버 {}", JUserDTO);
        return JUserDTO;
    }
}
