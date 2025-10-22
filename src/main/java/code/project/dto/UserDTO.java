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
public class UserDTO extends User {

    private String username, password, name, address, email;

    private Integer age;

    private List<String> roleNames = new ArrayList<>();

    public UserDTO(String username, String password, String name, String address, Integer age, String email, List<String> roleNames) {
        super(
                username,
                password,
                roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList()));

        this.username = username;
        this.password = password;
        this.name = name;
        this.address = address;
        this.age = age;
        this.email = email;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("username", username);
        dataMap.put("password", password);
        dataMap.put("name", name);
        dataMap.put("address", address);
        dataMap.put("age", age);
        dataMap.put("email", email);
        dataMap.put("roleNames", roleNames);

        return dataMap;
    }
}
