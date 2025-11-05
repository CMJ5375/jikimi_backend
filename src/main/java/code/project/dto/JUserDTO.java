package code.project.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class JUserDTO extends User {
    private Long userId;
    private String username, password, name, address, email, profileImage;
    private Integer age;
    private List<String> roleNames = new ArrayList<>();

    public JUserDTO(String username, String password, String name, String address,
                    Integer age, String email, String profileImage, List<String> roleNames) {
        super(
                username,
                password,
                (roleNames == null
                        ? Collections.emptyList()
                        : roleNames.stream()
                        .map(str -> new SimpleGrantedAuthority("ROLE_" + str))
                        .collect(Collectors.toList()))
        );
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.age = age;
        this.email = email;
        this.profileImage = profileImage;
        this.roleNames = roleNames;
    }

    // 추가: 예전 시그니처(프로필 이미지 없음)도 지원
    public JUserDTO(String username, String password, String name, String address,
                    Integer age, String email, List<String> roleNames) {
        this(username, password, name, address, age, email, null, roleNames);
    }

    // id(userId) 필드 추가
    public JUserDTO(Long userId, String username, String password, String name, String address,
                    Integer age, String email, String profileImage, List<String> roleNames) {
        this(username, password, name, address, age, email, profileImage, roleNames);
        this.userId = userId;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("userId", userId);
        dataMap.put("username", username);
        dataMap.put("password", password);
        dataMap.put("name", name);
        dataMap.put("address", address);
        dataMap.put("age", age);
        dataMap.put("email", email);
        dataMap.put("profileImage", profileImage); // 누락돼 있었다면 추가
        dataMap.put("roleNames", roleNames);
        return dataMap;
    }
}