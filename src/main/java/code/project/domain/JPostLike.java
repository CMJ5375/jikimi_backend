package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "j_post_like")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class JPostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_like_number")
    private Long post_like_number;

    // 어떤 글에 대한 좋아요인가
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private JPost post;

    // 누가 눌렀나
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private JUser user;
}