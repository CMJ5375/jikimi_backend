package code.project.service;

import code.project.domain.*;
import code.project.repository.HospitalRepository;
import code.project.repository.JUserFavoriteRepository;
import code.project.repository.JUserRepository;
import code.project.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JUserFavoriteServiceImpl implements JUserFavoriteService {

    private final JUserRepository userRepository;
    private final JUserFavoriteRepository favoriteRepository;
    private final HospitalRepository hospitalRepository;
    private final PharmacyRepository pharmacyRepository;

    private JUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    // 내 즐겨찾기 ID 리스트
    @Override
    public List<Long> getMyFavoriteIds(String username, FacilityType type) {
        JUser user = getUser(username);
        if (type == FacilityType.HOSPITAL) {
            return favoriteRepository.findHospitalIdsByUserId(user.getUserId());
        } else if (type == FacilityType.PHARMACY) {
            return favoriteRepository.findPharmacyIdsByUserId(user.getUserId());
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    // 즐겨찾기 추가
    @Override
    @Transactional
    public void addFavorite(String username, FacilityType type, Long targetId) {
        JUser user = getUser(username);

        if (type == FacilityType.HOSPITAL) {
            if (favoriteRepository.existsByUser_UserIdAndHospital_HospitalId(user.getUserId(), targetId)) return;
            Hospital hospital = hospitalRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + targetId));
            favoriteRepository.save(JUserFavorite.ofHospital(user, hospital));
            return;
        }

        if (type == FacilityType.PHARMACY) {
            if (favoriteRepository.existsByUser_UserIdAndPharmacy_PharmacyId(user.getUserId(), targetId)) return;
            Pharmacy pharmacy = pharmacyRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("Pharmacy not found: " + targetId));
            favoriteRepository.save(JUserFavorite.ofPharmacy(user, pharmacy));
            return;
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    // 즐겨찾기 삭제
    @Override
    @Transactional
    public void removeFavorite(String username, FacilityType type, Long targetId) {
        JUser user = getUser(username);

        if (type == FacilityType.HOSPITAL) {
            favoriteRepository.deleteByUser_UserIdAndHospital_HospitalId(user.getUserId(), targetId);
            return;
        }
        if (type == FacilityType.PHARMACY) {
            favoriteRepository.deleteByUser_UserIdAndPharmacy_PharmacyId(user.getUserId(), targetId);
            return;
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    // 즐겨찾기 여부 확인 (토글용)
    @Override
    public boolean isFavorite(String username, FacilityType type, Long targetId) {
        JUser user = getUser(username);
        if (type == FacilityType.HOSPITAL) {
            return favoriteRepository.existsByUser_UserIdAndHospital_HospitalId(user.getUserId(), targetId);
        } else if (type == FacilityType.PHARMACY) {
            return favoriteRepository.existsByUser_UserIdAndPharmacy_PharmacyId(user.getUserId(), targetId);
        }
        return false;
    }
}
