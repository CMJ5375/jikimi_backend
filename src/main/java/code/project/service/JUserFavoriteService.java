package code.project.service;

import code.project.domain.FacilityType;
import code.project.dto.HospitalDTO;
import code.project.dto.PharmacyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JUserFavoriteService {

    // 내 즐겨찾기 리스트 조회(병원, 약국)
    List<Long> getMyFavoriteIds(String username, FacilityType type);

    // 즐겨찾기 추가
    void addFavorite(String username, FacilityType type, Long targetId);

    // 즐겨찾기 삭제
    void removeFavorite(String username, FacilityType type, Long targetId);

    // 즐겨찾기 여부 확인
    boolean isFavorite(String username, FacilityType type, Long targetId);

    // 마이페이지 병원 즐겨찾기 (페이지네이션)
    Page<HospitalDTO> getMyHospitalFavorites(String username, Pageable pageable);

    // 마이페이지 약국 즐겨찾기 (페이지네이션)
    Page<PharmacyDTO> getMyPharmacyFavorites(String username, Pageable pageable);
}
