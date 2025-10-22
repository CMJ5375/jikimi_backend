package code.project.dto;

import code.project.domain.FacilityBusinessHour;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityBusinessHourDTO {
    private String dayOfWeek;
    private String openTime;
    private String closeTime;
    private boolean closed;
    private boolean open24h;
    private String note;

    public static FacilityBusinessHourDTO fromEntity(FacilityBusinessHour entity) {
        return FacilityBusinessHourDTO.builder()
                .dayOfWeek(entity.getDayOfWeek().name())
                .openTime(entity.getOpenTime() != null ? entity.getOpenTime().toString() : null)
                .closeTime(entity.getCloseTime() != null ? entity.getCloseTime().toString() : null)
                .closed(entity.isClosed())
                .open24h(entity.isOpen24h())
                .note(entity.getNote())
                .build();
    }
}
