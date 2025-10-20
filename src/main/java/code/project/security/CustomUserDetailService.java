package code.project.security;

import code.project.domain.User;
import code.project.dto.UserDTO;
import code.project.repository.UserRepository;
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

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("시큐리티가 사용자 정보조회, 처리하나?, 사용자이름은? {}", username);
        User user = userRepository.getwithRoles(username);
//        멤버는 entity이고 우리가 반환해야하는 것은 userDTO자료형이다.
        if(user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        UserDTO userDTO = new UserDTO(
                user.getUsername(),
                user.getPassword(),
                user.getName(),
                user.getAddress(),
                user.getAge(),
                user.getEmail(),
                user.getSocialType(),
                user.getMemberRoleList().stream().map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );
        log.info("로그인한 멤버 {}", userDTO);
        return userDTO;
    }
}
