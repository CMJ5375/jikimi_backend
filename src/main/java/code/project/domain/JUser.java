package code.project.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "j_user") // 예약어 충돌 방지
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString

public class JUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 50 ,unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String address;

    private Integer age;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    // 프로필
    @Column(length = 255)
    private String profileImage;

    public void updateProfile(String name, String address, Integer age, String profileImage) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (age != null) this.age = age;
        if (profileImage != null) this.profileImage = profileImage;
    }

    // 'LOCAL', 'KAKAO', 'GOOGLE' 중 하나
    @Column(nullable = false, length = 10)
    @Builder.Default
    private String socialType = "LOCAL";

    //등급 권한 배열형으로 수정
    //memberRoleList가 실제로 사용될 때 데이터를 로드
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "juser_jmember_role_list",
            joinColumns = @JoinColumn(name = "juser_user_id")
    )
    @Column(name = "jmember_role_list", nullable = false)
    @Enumerated(EnumType.ORDINAL) // ★ 숫자(0=USER, 1=ADMIN)로 저장/검증
    @Builder.Default
    private List<JMemberRole> JMemberRoleList = new ArrayList<>();

    //권한부여
    public void addRole(JMemberRole JMemberRole){
        JMemberRoleList.add(JMemberRole);
    }

    //권한삭제
    public void clearRole(){
        JMemberRoleList.clear();
    }
}