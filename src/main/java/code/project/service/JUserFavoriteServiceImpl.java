package code.project.service;

import code.project.domain.*;
import code.project.dto.HospitalDTO;
import code.project.dto.PharmacyDTO;
import code.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JUserFavoriteServiceImpl implements JUserFavoriteService {

    private final JUserFavoriteRepository favoriteRepository;
    private final JUserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final PharmacyRepository pharmacyRepository;

    // username → JUser 조회
    private JUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    // 내 즐겨찾기 ID 리스트 조회
    @Override
    public List<Long> getMyFavoriteIds(String username, FacilityType type) {
        JUser user = getUser(username);
        if (type == FacilityType.HOSPITAL) {
            return favoriteRepository.findHospitalIdsByUserId(user.getUserId());
        } else if (type == FacilityType.PHARMACY) {
            return favoriteRepository.findPharmacyIdsByUserId(user.getUserId());
        }
        throw new IllegalArgumentException("Unsupported FacilityType: " + type);
    }

    // 즐겨찾기 추가
    @Override
    @Transactional
    public void addFavorite(String username, FacilityType type, Long targetId) {
        JUser user = getUser(username);

        if (type == FacilityType.HOSPITAL) {
            boolean exists = favoriteRepository.existsByUser_UserIdAndHospital_HospitalId(user.getUserId(), targetId);
            if (!exists) {
                Hospital hospital = hospitalRepository.findById(targetId)
                        .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + targetId));
                JUserFavorite favorite = JUserFavorite.builder()
                        .user(user)
                        .hospital(hospital)
                        .type(FacilityType.HOSPITAL)
                        .build();
                favoriteRepository.save(favorite);
            }
        } else if (type == FacilityType.PHARMACY) {
            boolean exists = favoriteRepository.existsByUser_UserIdAndPharmacy_PharmacyId(user.getUserId(), targetId);
            if (!exists) {
                Pharmacy pharmacy = pharmacyRepository.findById(targetId)
                        .orElseThrow(() -> new IllegalArgumentException("Pharmacy not found: " + targetId));
                JUserFavorite favorite = JUserFavorite.builder()
                        .user(user)
                        .pharmacy(pharmacy)
                        .type(FacilityType.PHARMACY)
                        .build();
                favoriteRepository.save(favorite);
            }
        } else {
            throw new IllegalArgumentException("Unsupported FacilityType: " + type);
        }
    }

    // 즐겨찾기 삭제
    @Override
    @Transactional
    public void removeFavorite(String username, FacilityType type, Long targetId) {
        JUser user = getUser(username);

        if (type == FacilityType.HOSPITAL) {
            favoriteRepository.deleteByUser_UserIdAndHospital_HospitalId(user.getUserId(), targetId);
        } else if (type == FacilityType.PHARMACY) {
            favoriteRepository.deleteByUser_UserIdAndPharmacy_PharmacyId(user.getUserId(), targetId);
        } else {
            throw new IllegalArgumentException("Unsupported FacilityType: " + type);
        }
    }

    // 즐겨찾기 여부 확인
    @Override
    public boolean isFavorite(String username, FacilityType type, Long targetId) {
        JUser user = getUser(username);

        if (type == FacilityType.HOSPITAL) {
            return favoriteRepository.existsByUser_UserIdAndHospital_HospitalId(user.getUserId(), targetId);
        } else if (type == FacilityType.PHARMACY) {
            return favoriteRepository.existsByUser_UserIdAndPharmacy_PharmacyId(user.getUserId(), targetId);
        }
        throw new IllegalArgumentException("Unsupported FacilityType: " + type);
    }

    // 마이페이지 병원 즐겨찾기 (페이징)
    @Override
    public Page<HospitalDTO> getMyHospitalFavorites(String username, Pageable pageable) {
        return favoriteRepository.findHospitalsPageByUsername(username, pageable)
                .map(HospitalDTO::fromEntity);
    }

    // 마이페이지 약국 즐겨찾기 (페이징)
    @Override
    public Page<PharmacyDTO> getMyPharmacyFavorites(String username, Pageable pageable) {
        return favoriteRepository.findPharmaciesPageByUsername(username, pageable)
                .map(PharmacyDTO::fromEntity);
    }
}
