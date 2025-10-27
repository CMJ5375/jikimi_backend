package code.project.service;

import code.project.domain.Facility;
import code.project.domain.FacilityType;
import code.project.domain.JUser;
import code.project.domain.JUserFavorite;
import code.project.repository.FacilityRepository;
import code.project.repository.JUserFavoriteRepository;
import code.project.repository.JUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JUserFavoriteServiceImpl implements JUserFavoriteService {

    private final JUserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final JUserFavoriteRepository favoriteRepository;

    private JUser getUserByUsername(String username) {
        return userRepository.getCodeUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMyFavoriteFacilityIds(String username, FacilityType type) {
        JUser user = getUserByUsername(username);
        // JUserFavorite 리스트로 받아서 FacilityType 기준으로 필터링
        return favoriteRepository.findById_UserId(user.getUserId())
                .stream()
                .map(uf -> {
                    Facility f = uf.getFacility();
                    if (type == FacilityType.HOSPITAL && f.getType() == FacilityType.HOSPITAL) {
                        if (f.getHospital() != null) {
                            return f.getHospital().getHospitalId(); // hospitalId 반환
                        }
                    }
                    if (type == FacilityType.PHARMACY && f.getType() == FacilityType.PHARMACY) {
                        if (f.getPharmacy() != null) {
                            return f.getPharmacy().getPharmacyId(); // pharmacyId 반환
                        }
                    }
                    // 매핑이 아직 없거나 예외 케이스 → facilityId로 fallback
                    return f.getFacilityId();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isMyFavorite(String username, Long facilityId) {
        JUser user = getUserByUsername(username);
        return favoriteRepository.existsById_UserIdAndId_FacilityId(user.getUserId(), facilityId);
    }

    @Override
    @Transactional
    public void addFavorite(String username, Long facilityId) {
        JUser user = getUserByUsername(username);
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found: " + facilityId));
        boolean exists = favoriteRepository.existsById_UserIdAndId_FacilityId(user.getUserId(), facilityId);
        if (!exists) {
            favoriteRepository.save(JUserFavorite.of(user, facility));
        }
    }

    @Override
    @Transactional
    public void removeFavorite(String username, Long facilityId) {
        JUser user = getUserByUsername(username);
        favoriteRepository.deleteById_UserIdAndId_FacilityId(user.getUserId(), facilityId);
    }
}
