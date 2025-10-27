package code.project.service;

import code.project.domain.FacilityType;
import java.util.List;

public interface JUserFavoriteService {

    List<Long> getMyFavoriteFacilityIds(String username, FacilityType type);

    boolean isMyFavorite(String username, Long facilityId);

    void addFavorite(String username, Long facilityId);

    void removeFavorite(String username, Long facilityId);
}
