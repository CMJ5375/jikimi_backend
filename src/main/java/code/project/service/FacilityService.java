package code.project.service;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import code.project.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public List<Facility> getAllFacilities() {
        return facilityRepository.findAll();
    }

    public List<Facility> getFacilitiesByType(FacilityType type) {
        return facilityRepository.findByType(type);
    }

    // name, orgType, deptName 모두 고려한 검색
    public List<Facility> searchFacilities(FacilityType type, String name, String orgType, String deptName) {
        boolean noFilter = (isBlank(name) && isBlank(orgType) && isBlank(deptName));
        if (noFilter) {
            return facilityRepository.findByType(type);
        }
        return facilityRepository.searchFacilities(type,
                isBlank(name) ? null : name,
                isBlank(orgType) ? null : orgType,
                isBlank(deptName) ? null : deptName);
    }

    public Optional<Facility> getFacilityById(Long id) {
        return facilityRepository.findById(id);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
