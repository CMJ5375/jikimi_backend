package code.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PageRequestDTO {

    @Builder.Default private int page = 1;
    @Builder.Default private int size = 10;

    private String boardCategory; // 프론트에서 문자열로 넘어오는 값
    private String q;
}
