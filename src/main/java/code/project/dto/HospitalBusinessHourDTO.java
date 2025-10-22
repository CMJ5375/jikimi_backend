package code.project.dto;

import code.project.domain.HospitalBusinessHour;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalBusinessHourDTO {
    private String dayOfWeek;
    private String openTime;
    private String closeTime;

    public static HospitalBusinessHourDTO fromEntity(HospitalBusinessHour entity) {
        return HospitalBusinessHourDTO.builder()
                .dayOfWeek(entity.getDayOfWeek().name())
                .openTime(entity.getOpenTime())
                .closeTime(entity.getCloseTime())
                .build();
    }
}
