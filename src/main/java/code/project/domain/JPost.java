package code.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "j_post")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = {"user", "likes"})
public class JPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id") // ← 스키마와 일치
    private Long postId;

    // 게시판 카테고리(문자열로 저장)
    @Enumerated(EnumType.STRING)
    @Column(name = "board_category", nullable = false, length = 30)
    private BoardCategory boardCategory;

    // 작성자 FK
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private JUser user;

    // 제목
    @Column(nullable = false, length = 200)
    private String title;

    // 내용(TEXT). columnDefinition은 불필요(백틱 이슈 방지)
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    // 첨부 (스키마가 file_url이면 name 명시)
    @Column(name = "file_url", length = 255)
    private String fileUrl;

    // 좋아요 수
    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    // 작성일
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 삭제 여부 (TINYINT(1) 매핑)
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // 조회수
    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.REMOVE,   // 삭제 시 좋아요 같이 제거
            orphanRemoval = true
    )
    private List<JPostLike> likes = new ArrayList<>();

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (likeCount == null) likeCount = 0;
        if (isDeleted == null) isDeleted = false;
        if (viewCount == null) viewCount = 0;
    }
}
