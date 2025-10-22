package code.project.dto;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import lombok.*;

import java.math.BigDecimal;

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

    // Entity → DTO 변환 메서드
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
                .build();
    }
}
