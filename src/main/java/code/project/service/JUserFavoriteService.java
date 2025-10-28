package code.project.service;

import code.project.domain.FacilityType;

import java.util.List;

public interface JUserFavoriteService {

    // 내 즐겨찾기 ID 리스트
    List<Long> getMyFavoriteIds(String username, FacilityType type);

    // 즐겨찾기 추가
    void addFavorite(String username, FacilityType type, Long targetId);

    // 즐겨찾기 삭제
    void removeFavorite(String username, FacilityType type, Long targetId);

    // 즐겨찾기 여부 확인 (토글용)
    boolean isFavorite(String username, FacilityType type, Long targetId);
}
