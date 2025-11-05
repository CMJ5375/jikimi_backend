package code.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "support")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = {"JUser", "likes"})
public class JSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supportId;

    // 작성자 (ADMIN이어야 함 — 서비스 레이어에서 권한 체크)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private JUser JUser;

    // 어떤 종류인지 구분 (NOTICE / FAQ / DATAROOM)
    @Column(nullable = false, length = 20)
    private String type;

    // 제목
    @Column(nullable = false, length = 200)
    private String title;

    // 내용
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 자료실 전용 첨부파일 이름
    @Column(length = 200)
    private String fileName;

    // 자료실 전용 첨부파일 경로
    @Column(length = 255)
    private String fileUrl;

    // 상단 고정 기능 (공지/자료실용)
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean pinnedCopy;

    // 고정 사본의 원본 ID
    @Column
    private Long originalId;

    // 조회수
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer viewCount;

    // 날짜
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 좋아요
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likeCount;

    @PrePersist
    void onCreate() {
        if (viewCount == null) viewCount = 0;
        if (likeCount == null) likeCount = 0;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Integer getLikeCount() {
        return likeCount == null ? 0 : likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = (likeCount == null ? 0 : likeCount);
    }
}
