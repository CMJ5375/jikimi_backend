package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "support_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_support_like_support_user", columnNames = {"support_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JSupportLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "support_id", nullable = false)
    private JSupport support;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private JUser user;

    @Override
    public String toString() {
        return "JSupportLike(id=" + id +
                ", supportId=" + (support != null ? support.getSupportId() : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ")";
    }
}
