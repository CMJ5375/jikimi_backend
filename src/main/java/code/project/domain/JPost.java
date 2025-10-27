package code.project.domain;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Post")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class JPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "board_id", nullable = false)
//    private Board board;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id") // NULL 허용
//    private BoardCategory category;

    //    보드 이넘타입으로 바꿈
    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    //작성자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private JUser user;

    //제목
    @Column(nullable = false, length = 200)
    private String title;

    //내용
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //첨부파일
    @Column(length = 255)
    private String fileUrl;

    //좋아요 수
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer likeCount;

    //작성일
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    //삭제 여부
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isDeleted;

    //조회수
    @Column(nullable = false, columnDefinition = "int default 0")
    private int viewCount;

    // 생성시 자동 설정되게
    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (likeCount == null) likeCount = 0;
        if (isDeleted == null) isDeleted = false;
        if (viewCount == null) viewCount = 0;
    }
}