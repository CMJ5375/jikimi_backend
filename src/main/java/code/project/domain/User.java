package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`User`") // 예약어 충돌 방지
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // 'LOCAL', 'KAKAO', 'GOOGLE' 중 하나
    @Column(nullable = false, length = 10)
    private String socialType = "LOCAL";


    //등급 권한 배열형으로 수정
    //memberRoleList가 실제로 사용될 때 데이터를 로드
    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    //권한부여
    public void addRole(MemberRole memberRole){
        memberRoleList.add(memberRole);
    }

    //권한삭제
    public void clearRole(){
        memberRoleList.clear();
    }
}
