package code.project.dto;

import code.project.domain.BoardCategory;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class JPostDTO {
        private Long postId;
        private BoardCategory boardCategory;
        private String title;
        private String content;
        private String fileUrl; //첨부파일
        private Integer likeCount;
        private LocalDateTime createdAt;
        private Boolean isDeleted;
        private Integer viewCount;
        private Integer commentCount;
        // 연관 객체는 ID나 이름만 노출
        private Long userId;
        private String authorName;

        //게시글 작성자가 관리자인지 일반 유저인지
        private String authorUsername;

        // 이 글에 좋아요 누른 사용자 username 목록
        private java.util.List<String> likedUsernames;

}
