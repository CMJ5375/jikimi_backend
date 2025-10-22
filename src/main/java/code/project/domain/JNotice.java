package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Notice")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder @ToString(exclude = "user")
public class JNotice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    // 작성자(ADMIN이어야 함 — 서비스 레이어에서 체크)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private JUser JUser;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 255)
    private String fileUrl;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer viewCount;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likeCount;

    @PrePersist
    void onCreate() {
        if (viewCount == null) viewCount = 0;
        if (likeCount == null) likeCount = 0;
    }

}
