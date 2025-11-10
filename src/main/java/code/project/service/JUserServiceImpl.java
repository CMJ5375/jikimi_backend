package code.project.service;

import code.project.domain.JMemberRole;
import code.project.domain.JUser;
import code.project.dto.JUserModifyDTO;
import code.project.dto.KakaoUserInfoDTO;
import code.project.dto.JUserDTO;
import code.project.repository.JUserRepository;
import code.project.util.CustomS3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JUserServiceImpl implements JUserService {

    private final JUserRepository jUserRepository;

    private final PasswordEncoder passwordEncoder;
    private final CustomS3Util s3Util;

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
    @Override
    public JUserDTO login(String username, String rawPassword) {
        var user = jUserRepository.getCodeUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user_not_found"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("bad_credentials");
        }
        return entityToDTO(user);
    }

    @Override
    public JUserDTO authenticate(String username, String rawPassword) {
        return jUserRepository.getCodeUserByUsername(username)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                .map(this::entityToDTO)
                .orElse(null);
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
@Transactional
public String register(JUserDTO dto) {
    // 중복 가드
    if (jUserRepository.existsByUsername(dto.getUsername())) {
        return "이미 존재하는 아이디입니다.";
    }

    // 비밀번호 인코딩
    String enc = passwordEncoder.encode(dto.getPassword());

    // 권한 보정
    List<String> roles = dto.getRoleNames();
    if (roles == null || roles.isEmpty()) {
        roles = new ArrayList<>();
        roles.add("USER");
    }

    // 엔티티 생성 (name/email NOT NULL 보정은 컨트롤러에서 이미 했지만, 한 번 더 안전망)
    JUser entity = JUser.builder()
            .username(dto.getUsername())
            .password(enc)
            .name( (dto.getName()==null || dto.getName().isBlank()) ? "사용자" : dto.getName() )
            .email((dto.getEmail()==null || dto.getEmail().isBlank()) ? (dto.getUsername()+"@local.local") : dto.getEmail())
            .address(null)
            .age(null)
            .socialType("LOCAL")
            .build();

    // 권한 매핑
    for (String r : roles) {
        entity.addRole(code.project.domain.JMemberRole.valueOf(r));
    }

    jUserRepository.save(entity);
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

    @Transactional
    @Override
    public JUserDTO updateProfile(String username, String name, String address, Integer age, MultipartFile image) {
        JUser user = jUserRepository.getCodeUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        String oldUrl = user.getProfileImage(); // 기존 이미지 URL (있으면 삭제 대상)
        String newUrl = null;

        try {
            if (image != null && !image.isEmpty()) {
                // 파일명/키 구성
                String original = image.getOriginalFilename();
                if (original == null || original.isBlank()) {
                    original = "profile.png";
                }
                // 파일명에 포함될 수 있는 경로 구분자 방어
                String safeName = original.replace("\\", "/");
                int slash = safeName.lastIndexOf('/');
                if (slash >= 0) safeName = safeName.substring(slash + 1);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String key = "profiles/" + user.getUserId() + "/profile_" + timestamp + "_" + safeName;

                // S3 업로드 (컨텐츠 타입 그대로 넘김)
                String contentType = image.getContentType();
                byte[] bytes = image.getBytes(); // 메모리에 적당한 크기면 OK

                s3Util.uploadBytes(bytes, key, contentType);

                // 공개 URL
                newUrl = s3Util.objectUrl(key);

                // 기존 S3 이미지 삭제 (동일 버킷 URL인 경우만)
                if (oldUrl != null && isS3UrlOfOurBucket(oldUrl)) {
                    String oldKey = extractS3KeyFromUrl(oldUrl);
                    if (oldKey != null && !oldKey.isBlank()) {
                        try {
                            s3Util.deleteByKey(oldKey);
                        } catch (Exception ex) {
                            log.warn("프로필 기존 S3 객체 삭제 실패 oldKey={}", oldKey, ex);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지 업로드 실패", e);
        }

        // 전달되지 않은 필드는 유지
        user.updateProfile(
                (name    != null ? name    : user.getName()),
                (address != null ? address : user.getAddress()),
                (age     != null ? age     : user.getAge()),
                (newUrl  != null ? newUrl  : user.getProfileImage())
        );

        jUserRepository.save(user);

        JUserDTO dto = entityToDTO(user);
        dto.setProfileImage(user.getProfileImage());
        return dto;
    }

    /** 우리 버킷의 S3 정적 URL인지 대략 검사 (예: https://<bucket>.s3.<region>.amazonaws.com/<key>) */
    private boolean isS3UrlOfOurBucket(String url) {
        try {
            URI u = URI.create(url);
            String host = u.getHost(); // e.g. elasticbeanstalk-...s3.ap-northeast-2.amazonaws.com
            return host != null && host.contains(".s3.") && host.endsWith(".amazonaws.com");
        } catch (Exception e) {
            return false;
        }
    }

    /** 정적 URL에서 key 추출 */
    private String extractS3KeyFromUrl(String url) {
        try {
            URI u = URI.create(url);
            String path = u.getPath(); // "/profiles/123/xxx.png"
            if (path == null || path.isBlank()) return null;
            // 맨 앞 슬래시 제거
            if (path.startsWith("/")) path = path.substring(1);
            // URL decoding (안전)
            return java.net.URLDecoder.decode(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }
}
