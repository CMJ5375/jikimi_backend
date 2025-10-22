package code.project.dto;

import code.project.domain.FacilityBusinessHour;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true) // 직렬화용(필드 final 아니면 생략 가능)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacilityBusinessHourDTO {

    private List<String> days;   // ["MON","TUE","WED",...]
    private String openTime;     // "HH:mm"
    private String closeTime;    // "HH:mm"
    private boolean closed;
    private boolean open24h;
    private String note;

    public static FacilityBusinessHourDTO fromEntity(FacilityBusinessHour entity) {
        List<String> dayList = (entity.getDays() == null || entity.getDays().isBlank())
                ? List.of()
                : Arrays.stream(entity.getDays().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");
        String open = entity.getOpenTime() != null ? entity.getOpenTime().format(HHMM) : null;
        String close = entity.getCloseTime() != null ? entity.getCloseTime().format(HHMM) : null;

        return FacilityBusinessHourDTO.builder()
                .days(dayList)
                .openTime(open)
                .closeTime(close)
                .closed(entity.isClosed())
                .open24h(entity.isOpen24h())
                .note(entity.getNote())
                .build();
    }
}
