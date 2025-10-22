package code.project.dto;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityDTO {

    private Long facilityId;
    private String name;
    private FacilityType type;
    private String phone;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String regionCode;
    private String orgType;

    private List<FacilityBusinessHourDTO> businessHours; // 추가됨

    public static FacilityDTO fromEntity(Facility facility) {
        return FacilityDTO.builder()
                .facilityId(facility.getFacilityId())
                .name(facility.getName())
                .type(facility.getType())
                .phone(facility.getPhone())
                .address(facility.getAddress())
                .latitude(facility.getLatitude())
                .longitude(facility.getLongitude())
                .regionCode(facility.getRegionCode())
                .orgType(facility.getOrgType())
                .businessHours(facility.getBusinessHours()
                        .stream()
                        .map(FacilityBusinessHourDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
