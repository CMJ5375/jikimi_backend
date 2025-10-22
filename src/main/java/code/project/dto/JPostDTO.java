package code.project.dto;

import code.project.domain.BoardCategory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JPostDTO {
        private Long postId;
        private BoardCategory boardCategory;
        private String title;
        private String content;
        private String fileUrl;
        private Integer likeCount;
        private LocalDateTime createdAt;
        private Boolean isDeleted;

        // 연관 객체는 ID나 이름만 노출
        private Long userId;
        //private String userName; //＊
}
