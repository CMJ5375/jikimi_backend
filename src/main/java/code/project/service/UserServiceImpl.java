package code.project.service;

import code.project.domain.MemberRole;
import code.project.domain.User;
import code.project.dto.KakaoUserInfoDTO;
import code.project.dto.UserDTO;
import code.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO getKakaoUser(String accessToken) {
        KakaoUserInfoDTO kakaoUserInfo = getNicknameFromAccessToken(accessToken);

        log.info("가져온 nickname : {}", kakaoUserInfo);

        Optional<User> result = userRepository.getCodeUserByUsername(kakaoUserInfo.getUsername());
        //기존회원
        if(result.isPresent()) {
            UserDTO userDTO = entityToDTO(result.get());
            return userDTO;
        }
        User socialUser = makeSocialUser(kakaoUserInfo.getUsername(), kakaoUserInfo.getName());
        userRepository.save(socialUser);
        UserDTO userDTO = entityToDTO(socialUser);
        return userDTO;
    }

    private KakaoUserInfoDTO getNicknameFromAccessToken(String accessToken) {
        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        if(accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(
                        uriBuilder.toString(),
                        HttpMethod.GET,
                        entity,
                        LinkedHashMap.class
                );

        log.info("response {}", response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        log.info("bodyMap : {}", bodyMap);
        log.info("id: {}", bodyMap.get("id"));
        String name = String.valueOf(bodyMap.get("id"));

        LinkedHashMap<String, String> properties = bodyMap.get("properties");
        log.info("nickname : {}", properties.get("nickname"));
        String username = properties.get("nickname");

        return new KakaoUserInfoDTO(username, name);
    }

//    해당 이메일을 가진 회원이 없다면 새로운 회원을 추가할 때 패스워드를 임의로 생성한다.
    private String makeTempPassword() {
        StringBuffer buffer = new StringBuffer();

        for(int i = 0; i < 10; i++) {
            buffer.append((char) ((int)(Math.random()*55) + 65));
        }

        return buffer.toString();
    }

//    소셜회원 만들기
    private User makeSocialUser(String username, String name) {
        String tempPassword = makeTempPassword();

        log.info("tempPassword: " + tempPassword);

        //회원만들기
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(tempPassword))
                .name(name)
                .email(username)
                .socialType("KAKAO")
                .build();
        user.addRole(MemberRole.USER);

        return user;
    }
}
