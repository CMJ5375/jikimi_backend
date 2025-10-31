package code.project.service;

import code.project.domain.FacilityBusinessHour;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.repository.FacilityBusinessHourRepository;
import code.project.util.OpenTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpenStatusService {

    private final FacilityBusinessHourRepository hourRepo;

    @Transactional(readOnly = true)
    public boolean isFacilityOpen(Long facilityId) {
        List<FacilityBusinessHourDTO> rows = hourRepo
                .findByFacility_FacilityIdOrderByIdAsc(facilityId)
                .stream()
                .map(FacilityBusinessHourDTO::fromEntity)
                .toList();
        return OpenTimeUtil.isOpenNow(rows);
    }

    @Transactional(readOnly = true)
    public Map<Long, Boolean> batchIsOpen(Collection<Long> facilityIds) {
        if (facilityIds == null || facilityIds.isEmpty()) return Collections.emptyMap();

        List<FacilityBusinessHour> all = hourRepo.findByFacility_FacilityIdInOrderByIdAsc(facilityIds);
        Map<Long, List<FacilityBusinessHourDTO>> grouped = all.stream()
                .collect(Collectors.groupingBy(
                        hb -> hb.getFacility().getFacilityId(),
                        Collectors.mapping(FacilityBusinessHourDTO::fromEntity, Collectors.toList())
                ));

        Map<Long, Boolean> result = new HashMap<>();
        for (Long fid : facilityIds) {
            List<FacilityBusinessHourDTO> rows = grouped.getOrDefault(fid, Collections.emptyList());
            result.put(fid, OpenTimeUtil.isOpenNow(rows));
        }
        return result;
    }
}
