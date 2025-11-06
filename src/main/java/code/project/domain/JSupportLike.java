package code.project.domain;

import jakarta.persistence.*;
import lombok.*;

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
    private Long supportLikeNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "support_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_supportlike_support",
                    foreignKeyDefinition =
                            "FOREIGN KEY (support_id) REFERENCES support(support_id) ON DELETE CASCADE"
            )
    )
    private JSupport support;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_supportlike_user",
                    foreignKeyDefinition =
                            "FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE"
            )
    )
    private JUser user;

    @Override
    public String toString() {
        return "JSupportLike(id=" + supportLikeNumber +
                ", supportId=" + (support != null ? support.getSupportId() : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ")";
    }
}
