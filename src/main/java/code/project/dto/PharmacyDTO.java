package code.project.dto;

import code.project.domain.Pharmacy;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyDTO {

    private Long pharmacyId;
    private String pharmacyName;
    private String businessHour;
    private FacilityDTO facility;

    public static PharmacyDTO fromEntity(Pharmacy entity) {
        return PharmacyDTO.builder()
                .pharmacyId(entity.getPharmacyId())
                .pharmacyName(entity.getPharmacyName())
                .businessHour(entity.getBusinessHour())
                .facility(FacilityDTO.fromEntity(entity.getFacility()))
                .build();
    }
}
