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
    private String businessHour;
    private boolean hasEmergency;

    private FacilityDTO facility; // 상위 facility 정보
    private List<HospitalDepartmentDTO> departments;
    private List<HospitalBusinessHourDTO> businessHours;
    private List<HospitalInstitutionDTO> institutions;

    public static HospitalDTO fromEntity(Hospital entity) {
        return HospitalDTO.builder()
                .hospitalId(entity.getHospitalId())
                .hospitalName(entity.getHospitalName())
                .businessHour(entity.getBusinessHour())
                .hasEmergency(entity.isHasEmergency())
                .facility(FacilityDTO.fromEntity(entity.getFacility()))
                .departments(entity.getDepartments()
                        .stream()
                        .map(HospitalDepartmentDTO::fromEntity)
                        .collect(Collectors.toList()))
                .businessHours(entity.getBusinessHours()
                        .stream()
                        .map(HospitalBusinessHourDTO::fromEntity)
                        .collect(Collectors.toList()))
                .institutions(entity.getInstitutions()
                        .stream()
                        .map(HospitalInstitutionDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
