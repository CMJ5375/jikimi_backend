package code.project.service;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.dto.FacilityDTO;
import code.project.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    @Transactional(readOnly = true)
    public Page<Facility> getFacilities(FacilityType type, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        if (keyword != null && !keyword.isEmpty()) {
            return facilityRepository.findByNameContainingAndType(keyword, type, pageable);
        } else {
            return facilityRepository.findByType(type, pageable);
        }
    }

    @Transactional(readOnly = true)
    public FacilityDTO getFacilityDTO(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found"));
        return entityToDTO(facility);
    }

    @Transactional(readOnly = true)
    public List<FacilityBusinessHourDTO> getBusinessHoursByFacilityId(Long facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new IllegalArgumentException("시설을 찾을 수 없습니다."));

        return facility.getBusinessHours()
                .stream()
                .map(FacilityBusinessHourDTO::fromEntity)
                .toList();
    }

    private FacilityDTO entityToDTO(Facility facility) {
        return FacilityDTO.builder()
                .facilityId(facility.getFacilityId())
                .name(facility.getName())
                .type(facility.getType())
                .phone(facility.getPhone())
                .address(facility.getAddress())
                .latitude(facility.getLatitude() != null ? facility.getLatitude().doubleValue() : null)
                .longitude(facility.getLongitude() != null ? facility.getLongitude().doubleValue() : null)
                .regionCode(facility.getRegionCode())
                .businessHours(
                        facility.getBusinessHours() != null
                                ? facility.getBusinessHours().stream()
                                .map(FacilityBusinessHourDTO::fromEntity)
                                .toList()
                                : null
                )
                .build();
    }
}
