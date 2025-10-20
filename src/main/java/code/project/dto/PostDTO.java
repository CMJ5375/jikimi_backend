package code.project.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
        private Long postId;
        private String title;
        private String content;
        private String fileUrl;
        private Integer likeCount;
        private LocalDateTime createdAt;
        private Boolean isDeleted;

        // 연관 객체는 ID나 이름만 노출
        private Long boardId;
        private Long categoryId;
        private Long userId;
        private String userName; //＊
}
