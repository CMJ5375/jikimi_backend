package code.project.service;

import code.project.dto.JSupportDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JSupportService {

    // type에 따라 NOTICE / FAQ / DATAROOM 구분
    Page<JSupportDTO> list(String type, String keyword, int page, int size);
    JSupportDTO get(Long id, boolean increaseView);
    Long create(JSupportDTO dto, Long adminId);
    void update(JSupportDTO dto, Long adminId);
    void delete(Long id, Long adminId);
    void pin(Long id, Long adminId);
    void unpin(Long pinnedId, Long adminId);
    List<JSupportDTO> getPinnedList(String type);

    // 좋아요 관련 메서드 (userId 기반)
    LikeResult toggleSupportLike(Long supportId, Long userId);
    boolean isSupportLikedByUser(Long supportId, Long userId);
    int getSupportLikeCount(Long supportId);

    // 좋아요 결과 DTO
    class LikeResult {
        private final int supportLikeCount;
        private final boolean liked;

        public LikeResult(int likeCount, boolean liked) {
            this.supportLikeCount = likeCount;
            this.liked = liked;
        }

        public int getLikeCount() {
            return supportLikeCount;
        }

        public boolean isLiked() {
            return liked;
        }
    }
}
