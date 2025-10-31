package code.project.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JSupportDTO {

    private Long supportId;
    private String type;                // NOTICE / FAQ / DATAROOM
    private String title;               // 제목
    private String content;             // 내용
    private String fileName;            // 자료실 전용
    private String fileUrl;             // 자료실 전용
    private boolean pinnedCopy;         // 상단 고정
    private Long originalId;            // 고정 사본 원본 ID
    private Integer viewCount;          // 조회수
    private LocalDateTime createdAt;    // 날짜
    private Long userId;                // 작성자 ID (ADMIN)
    private String username;            // 작성자 이름 (선택)
}
