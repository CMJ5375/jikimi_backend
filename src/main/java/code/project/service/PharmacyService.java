package code.project.service;

import code.project.domain.Pharmacy;
import code.project.dto.FacilityBusinessHourDTO;
import code.project.dto.PharmacyDTO;
import code.project.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    @Transactional(readOnly = true)
    public Page<PharmacyDTO> getPharmacyList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("pharmacyName").ascending());
        return pharmacyRepository.findAll(pageable)
                .map(PharmacyDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public PharmacyDTO getPharmacyDetail(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("약국을 찾을 수 없습니다."));
        return PharmacyDTO.fromEntity(pharmacy);
    }


    @Transactional(readOnly = true)
    public List<FacilityBusinessHourDTO> getFacilityBusinessHoursByPharmacyId(Long pharmacyId) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new IllegalArgumentException("약국을 찾을 수 없습니다."));

        return pharmacy.getFacility().getBusinessHours()
                .stream()
                .map(FacilityBusinessHourDTO::fromEntity)
                .toList();
    }

}
