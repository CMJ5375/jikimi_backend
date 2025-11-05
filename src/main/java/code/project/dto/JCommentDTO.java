package code.project.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class JCommentDTO {
    private Long commentId;
    private Long postId;
    private Long userId;      // 작성자 식별 (PostDTO와 동일 패턴)
    private String authorName; // UI 노출용 (username/name/email 앞부분)
    private String content;
    private LocalDateTime createdAt;
    private String authorProfileImage; //마이페이지 프로필
}
