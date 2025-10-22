package code.project.dto;

import code.project.domain.Pharmacy;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyDTO {

    private Long pharmacyId;
    private String pharmacyName;

    private FacilityDTO facility;
    private List<FacilityBusinessHourDTO> facilityBusinessHours;

    public static PharmacyDTO fromEntity(Pharmacy entity) {
        return PharmacyDTO.builder()
                .pharmacyId(entity.getPharmacyId())
                .pharmacyName(entity.getPharmacyName())
                .facility(FacilityDTO.fromEntity(entity.getFacility()))
                .facilityBusinessHours(entity.getFacility().getBusinessHours()
                        .stream()
                        .map(FacilityBusinessHourDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
