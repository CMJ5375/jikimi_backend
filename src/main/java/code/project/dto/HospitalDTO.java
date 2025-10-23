package code.project.dto;

import code.project.domain.Hospital;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalDTO {
    private Long hospitalId;
    private String hospitalName;
    private boolean hasEmergency;
    private Double distance; // 선택적
    private String orgType;
    private FacilityDTO facility;
    private List<String> departments;   // 유지
    private List<String> institutions; // 유지

    // 중복 가능성: FacilityDTO.businessHours에 이미 포함되므로 이 필드는 보통 제거 권장
    private List<FacilityBusinessHourDTO> facilityBusinessHours;

    public static HospitalDTO fromEntity(Hospital entity) {
        return HospitalDTO.builder()
                .hospitalId(entity.getHospitalId())
                .hospitalName(entity.getHospitalName())
                .orgType(entity.getOrgType())
                .hasEmergency(entity.isHasEmergency())
                .facility(FacilityDTO.fromEntity(entity.getFacility()))
                .departments(splitCsv(entity.getDepartmentsCsv()))
                .institutions(splitCsv(entity.getInstitutionsCsv()))
                // 필요 시 중복 제거: 아래 줄은 선택
                .facilityBusinessHours(
                        entity.getFacility().getBusinessHours().stream()
                                .map(FacilityBusinessHourDTO::fromEntity)
                                .collect(Collectors.toList())
                )
                .build();
    }

    // 같은 클래스 내부에 헬퍼 추가
    private static List<String> splitCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    public HospitalDTO withDistance(Double distanceKm) {
        this.distance = distanceKm;
        return this;
    }
}

