package code.project.dto;

import code.project.domain.Facility;
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

    // 약국 ID
    private Long pharmacyId;
    // 약국 이름
    private String pharmacyName;
    // 거리
    private String distance;
    // 시설 기본정보 (주소, 전화번호 등)
    private FacilityDTO facility;
    // 영업시간
    private List<FacilityBusinessHourDTO> facilityBusinessHours;

    // Entity → DTO 변환 메서드
    public static PharmacyDTO fromEntity(Pharmacy entity) {
        if (entity == null) return null;

        Facility facility = entity.getFacility();

        return PharmacyDTO.builder()
                .pharmacyId(entity.getPharmacyId())
                .pharmacyName(entity.getPharmacyName())
                .facility(facility != null ? FacilityDTO.fromEntity(facility) : null)
                .facilityBusinessHours(
                        facility != null && facility.getBusinessHours() != null
                                ? facility.getBusinessHours().stream()
                                .map(FacilityBusinessHourDTO::fromEntity)
                                .collect(Collectors.toList())
                                : List.of()
                )
                .build();
    }

    // 거리 단위 자동변환
    public PharmacyDTO withDistance(Double km) {
        if (km == null) return this;
        double v = km;
        if (v < 1) this.distance = (Math.round(v * 1000)) + "m";
        else this.distance = String.format("%.1fkm", v);
        return this;
    }
}
