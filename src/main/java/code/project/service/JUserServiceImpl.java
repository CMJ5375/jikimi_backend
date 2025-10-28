package code.project.service;

import code.project.domain.JMemberRole;
import code.project.domain.JUser;
import code.project.dto.JUserModifyDTO;
import code.project.dto.KakaoUserInfoDTO;
import code.project.dto.JUserDTO;
import code.project.repository.JUserRepository;
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
public class JUserServiceImpl implements JUserService {

    private final JUserRepository jUserRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public JUserDTO getKakaoUser(String accessToken) {
        KakaoUserInfoDTO kakaoUserInfo = getNicknameFromAccessToken(accessToken);

        log.info("가져온 nickname : {}", kakaoUserInfo);

        Optional<JUser> result = jUserRepository.getCodeUserByUsername(kakaoUserInfo.getUsername());
        //기존회원
        if(result.isPresent()) {
            JUserDTO JUserDTO = entityToDTO(result.get());
            return JUserDTO;
        }
        JUser socialJUser = makeSocialUser(kakaoUserInfo.getUsername(), kakaoUserInfo.getName());
        jUserRepository.save(socialJUser);
        JUserDTO JUserDTO = entityToDTO(socialJUser);
        return JUserDTO;
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
    private JUser makeSocialUser(String username, String name) {
        String tempPassword = makeTempPassword();

        log.info("tempPassword: " + tempPassword);

        //회원만들기
        JUser user = JUser.builder()
                .username(username)
                .password(passwordEncoder.encode(tempPassword))
                .name(username)
                .email(name)
                .socialType("KAKAO")
                .build();
        user.addRole(JMemberRole.USER);
        return user;
    }

//    회원가입 로직
    @Override
    public String register(JUserDTO jUserDTO) {
        // 사용자명 중복 검사
        if(jUserRepository.existsByUsername(jUserDTO.getUsername())) {
            return "이미 존재하는 회원입니다.";
        }

        JUser jUser = JUser.builder()
                .username(jUserDTO.getUsername())
                .email(jUserDTO.getEmail())
                .name(jUserDTO.getName())
                .password(passwordEncoder.encode(jUserDTO.getPassword()))
                .socialType("LOCAL")
                .build();
        jUser.addRole(JMemberRole.USER);

        jUserRepository.save(jUser);

        return "회원가입 성공";
    }

//    회원 정보 수정 로직
    @Override
    public void modifyUser(JUserModifyDTO jUserModifyDTO) {

        log.info("아이디 : {}", jUserModifyDTO.getUsername());

        JUser jUser = jUserRepository
                .getCodeUserByUsername(jUserModifyDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + jUserModifyDTO.getUsername()));

        // ✅ password: null/빈문자면 건드리지 않음 (비번 변경은 별도 화면)
        if (jUserModifyDTO.getPassword() != null && !jUserModifyDTO.getPassword().isBlank()) {
            jUser.setPassword(passwordEncoder.encode(jUserModifyDTO.getPassword()));
        }

        // ✅ email: null이면 스킵, 값이 오면 빈문자도 허용하지 않으려면 아래처럼 trim 검사
        if (jUserModifyDTO.getEmail() != null && !jUserModifyDTO.getEmail().isBlank()) {
            jUser.setEmail(jUserModifyDTO.getEmail().trim());
        }

        // ✅ address: null이면 스킵
        if (jUserModifyDTO.getAddress() != null) {
            String addr = jUserModifyDTO.getAddress().trim();
            // 빈문자 들어오면 기존값 유지하려면 if(!addr.isEmpty()) 조건
            if (!addr.isEmpty()) {
                jUser.setAddress(addr);
            }
        }

        // ✅ age: null이면 스킵 (프런트에서 "" → null로 보내는 게 안전)
        if (jUserModifyDTO.getAge() != null) {
            jUser.setAge(jUserModifyDTO.getAge());
        }

        jUserRepository.save(jUser);
    }
}
