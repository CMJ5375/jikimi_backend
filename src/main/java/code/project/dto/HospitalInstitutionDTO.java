package code.project.dto;

import code.project.domain.HospitalInstitution;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalInstitutionDTO {
    private String resourceName;

    public static HospitalInstitutionDTO fromEntity(HospitalInstitution entity) {
        return HospitalInstitutionDTO.builder()
                .resourceName(entity.getResourceName())
                .build();
    }
}
