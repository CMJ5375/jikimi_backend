package code.project.dto;

import code.project.domain.Hospital;
import lombok.*;

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

    private FacilityDTO facility; // 상위 facility 정보
    private List<HospitalDepartmentDTO> departments;
    private List<HospitalInstitutionDTO> institutions;

    // Facility의 영업시간도 함께 포함하고 싶다면 ↓ 추가
    private List<FacilityBusinessHourDTO> facilityBusinessHours;

    public static HospitalDTO fromEntity(Hospital entity) {
        return HospitalDTO.builder()
                .hospitalId(entity.getHospitalId())
                .hospitalName(entity.getHospitalName())
                .hasEmergency(entity.isHasEmergency())
                .facility(FacilityDTO.fromEntity(entity.getFacility()))
                .departments(entity.getDepartments()
                        .stream()
                        .map(HospitalDepartmentDTO::fromEntity)
                        .collect(Collectors.toList()))
                .institutions(entity.getInstitutions()
                        .stream()
                        .map(HospitalInstitutionDTO::fromEntity)
                        .collect(Collectors.toList()))
                // 병원/약국 공통 영업시간은 Facility에서 가져오기
                .facilityBusinessHours(entity.getFacility().getBusinessHours()
                        .stream()
                        .map(FacilityBusinessHourDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
