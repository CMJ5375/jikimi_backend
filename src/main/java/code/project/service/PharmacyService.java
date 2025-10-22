package code.project.service;

import code.project.domain.Pharmacy;
import code.project.dto.PharmacyDTO;
import code.project.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;

    // 약국 목록 조회 (페이징)
    public Page<PharmacyDTO> getPharmacyList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("pharmacyName").ascending());
        return pharmacyRepository.findAll(pageable)
                .map(PharmacyDTO::fromEntity);
    }

    // 약국 상세 조회
    public PharmacyDTO getPharmacyDetail(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("약국을 찾을 수 없습니다."));
        return PharmacyDTO.fromEntity(pharmacy);
    }
}
