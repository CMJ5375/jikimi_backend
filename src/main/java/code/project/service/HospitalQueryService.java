package code.project.service;

import code.project.repository.HospitalRepository;
import code.project.service.HospitalListView; // 네가 만든 프로젝션 경로
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalQueryService {

    private final HospitalRepository hospitalRepository;

    /** 경량 목록 조회 (필터/거리 정렬 포함) */
    public Page<HospitalListView> getListLite(
            String keyword,
            String dept,
            String org,
            Boolean emergency,
            Double lat,
            Double lng,
            Pageable pageable
    ) {
        return hospitalRepository.findListLite(
                keyword, dept, org, emergency, lat, lng, pageable
        );
    }

    /** 즐겨찾기 경량 목록 (username 기준) */
    public Page<HospitalListView> getFavoriteListLite(
            String username,
            String keyword,
            String dept,
            String org,
            Boolean emergency,
            Pageable pageable
    ) {
        return hospitalRepository.findFavoriteListLite(
                username, keyword, dept, org, emergency, pageable
        );
    }
}
