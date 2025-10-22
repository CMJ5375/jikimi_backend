package code.project.service;

import code.project.domain.Facility;
import code.project.domain.FacilityBusinessHour;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.repository.FacilityBusinessHourRepository;
import code.project.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityBusinessHourService {

    private final FacilityBusinessHourRepository hourRepository;

    @Transactional(readOnly = true)
    public List<FacilityBusinessHourDTO> getBusinessHours(Long facilityId) {
        return hourRepository.findByFacility_FacilityIdOrderByIdAsc(facilityId)
                .stream()
                .map(FacilityBusinessHourDTO::fromEntity)
                .toList();
    }
}

