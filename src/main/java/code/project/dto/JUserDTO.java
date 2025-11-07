package code.project.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString(exclude = "password")            // 비번 노출 방지
@JsonIgnoreProperties(ignoreUnknown = true) // 예상 밖 키 무시
public class JUserDTO extends User {
    private Long userId;
    private String username, password, name, address, email, profileImage;
    private Integer age;
    private List<String> roleNames = new ArrayList<>();

    /** Jackson용 기본 생성자 (반드시 super 호출) */
    public JUserDTO() {
        super("", "", Collections.emptyList());
    }

    /** null/빈값 방어용 */
    private static String nz(String s) { return (s == null) ? "" : s; }

    private static Collection<SimpleGrantedAuthority> toAuthorities(List<String> roles) {
        if (roles == null) return Collections.emptyList();
        return roles.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.startsWith("ROLE_") ? s : "ROLE_" + s)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @JsonCreator
    public JUserDTO(
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("name") String name,
            @JsonProperty("address") String address,
            @JsonProperty("age") Integer age,
            @JsonProperty("email") String email,
            @JsonProperty("profileImage") String profileImage,
            @JsonProperty("roleNames") List<String> roleNames
    ) {
        super(nz(username), nz(password), toAuthorities(roleNames));
        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.age = age;
        this.email = email;
        this.profileImage = profileImage;
        this.roleNames = (roleNames == null) ? new ArrayList<>() : new ArrayList<>(roleNames);
    }

    // 예전 시그니처(프로필 이미지 없음) 지원
    public JUserDTO(String username, String password, String name, String address,
                    Integer age, String email, List<String> roleNames) {
        this(username, password, name, address, age, email, null, roleNames);
    }

    // id(userId) 포함 시그니처
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
        dataMap.put("profileImage", profileImage);
        dataMap.put("roleNames", roleNames);
        return dataMap;
    }

}
