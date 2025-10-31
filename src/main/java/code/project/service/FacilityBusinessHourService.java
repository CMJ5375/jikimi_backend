// src/main/java/code/project/service/FacilityBusinessHourService.java
package code.project.service;

import code.project.domain.FacilityBusinessHour;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.repository.FacilityBusinessHourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityBusinessHourService {

    private final FacilityBusinessHourRepository repo;

    @Transactional(readOnly = true)
    public List<FacilityBusinessHourDTO> getBusinessHours(Long facilityId) {
        try {
            List<FacilityBusinessHour> rows =
                    repo.findByFacility_FacilityIdOrderByIdAsc(facilityId);

            if (rows == null || rows.isEmpty()) return Collections.emptyList();

            return rows.stream()
                    .map(FacilityBusinessHourDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
