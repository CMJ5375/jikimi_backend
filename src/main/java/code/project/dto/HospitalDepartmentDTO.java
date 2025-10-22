package code.project.dto;

import code.project.domain.HospitalDepartment;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalDepartmentDTO {
    private String departmentName;

    public static HospitalDepartmentDTO fromEntity(HospitalDepartment entity) {
        return HospitalDepartmentDTO.builder()
                .departmentName(entity.getDepartmentName())
                .build();
    }
}
