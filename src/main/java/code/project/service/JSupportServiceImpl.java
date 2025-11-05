package code.project.service;

import code.project.domain.JSupport;
import code.project.domain.JSupportLike;
import code.project.domain.JUser;
import code.project.dto.JSupportDTO;
import code.project.repository.JSupportLikeRepository;
import code.project.repository.JSupportRepository;
import code.project.repository.JUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class JSupportServiceImpl implements JSupportService {

    private final JSupportRepository supportRepo;
    private final JUserRepository userRepo;
    private final JSupportLikeRepository supportLikeRepository;

    private JSupportDTO toDTO(JSupport e) {
        return JSupportDTO.builder()
                .supportId(e.getSupportId())
                .type(e.getType())
                .title(e.getTitle())
                .content(e.getContent())
                .fileName(e.getFileName())
                .fileUrl(e.getFileUrl())
                .pinnedCopy(e.isPinnedCopy())
                .originalId(e.getOriginalId())
                .viewCount(e.getViewCount())
                .likeCount(e.getLikeCount())
                .createdAt(e.getCreatedAt())
                .userId(e.getJUser().getUserId())
                .username(e.getJUser().getUsername())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JSupportDTO> list(String type, String keyword, int page, int size) {
        Page<JSupport> result;
        Pageable pageable = PageRequest.of(Math.max(0, page), size);

        if (keyword == null || keyword.trim().isEmpty()) {
            result = supportRepo.findByTypeOrderByPinned(type, pageable);
        } else {
            result = supportRepo.searchByTypeAndKeyword(type, keyword.trim(), pageable);
        }
        return result.map(this::toDTO);
    }

    @Override
    public JSupportDTO get(Long id, boolean increaseView) {
        JSupport entity = supportRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Support not found: " + id));
        if (increaseView && ("notice".equalsIgnoreCase(entity.getType())
                || "dataroom".equalsIgnoreCase(entity.getType()))) {
            entity.setViewCount(entity.getViewCount() + 1);
        }
        long likeCount = supportLikeRepository.countBySupport_SupportId(entity.getSupportId());
        entity.setLikeCount((int) likeCount);

        return toDTO(entity);
    }

    @Override
    public Long create(JSupportDTO dto, Long adminId) {
        if (adminId == null || adminId <= 0) {
            throw new IllegalArgumentException("adminId is required");
        }
        JUser admin = userRepo.findById(adminId)
                .orElseThrow(() -> new NoSuchElementException("Admin not found: " + adminId));

        String type = dto.getType() != null ? dto.getType().toLowerCase() : "notice";

        JSupport entity = JSupport.builder()
                .JUser(admin)
                .type(type)
                .title(dto.getTitle())
                .content(dto.getContent())
                .fileUrl(dto.getFileUrl())
                .fileName(dto.getFileName())
                .viewCount(0)
                .pinnedCopy(false)
                .originalId(null)
                .build();
        entity.setType(entity.getType().toLowerCase());

        supportRepo.save(entity);
        return entity.getSupportId();
    }

    @Override
    public void update(JSupportDTO dto, Long adminId) {
        JSupport entity = supportRepo.findById(dto.getSupportId()).orElseThrow();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setFileUrl(dto.getFileUrl());
        entity.setFileName(dto.getFileName());
    }

    @Override
    public void delete(Long id, Long adminId) {
        JSupport entity = supportRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Support not found: " + id));

        // 원본글인 경우 연결된 복제본도 함께 삭제
        if (!entity.isPinnedCopy()) {
            List<JSupport> copies = supportRepo.findAll()
                    .stream()
                    .filter(s -> s.getOriginalId() != null && s.getOriginalId().equals(id))
                    .toList();

            for (JSupport copy : copies) {
                supportRepo.delete(copy);
            }
        }
        supportRepo.delete(entity);
    }

    @Override
    public void pin(Long id, Long adminId) {
        JSupport origin = supportRepo.findById(id).orElseThrow();
        String type = origin.getType();

        // 원본 글도 pinned 상태로 표시되게 변경
        origin.setPinnedCopy(true);
        supportRepo.save(origin);

        long count = supportRepo.countByTypeAndPinnedCopyTrue(type);
        if (count >= 5) {
            List<JSupport> olds = supportRepo.findTop5ByTypeAndPinnedCopyIsTrueOrderByCreatedAtAsc(type);
            if (!olds.isEmpty()) supportRepo.delete(olds.get(0));
        }

        JSupport copy = JSupport.builder()
                .JUser(origin.getJUser())
                .type(type)
                .title(origin.getTitle())
                .content(origin.getContent())
                .fileUrl(origin.getFileUrl())
                .fileName(origin.getFileName())
                .pinnedCopy(true)
                .originalId(origin.getSupportId())
                .build();
        supportRepo.save(copy);
    }

    @Override
    public void unpin(Long pinnedId, Long adminId) {
        JSupport pinned = supportRepo.findById(pinnedId)
                .orElseThrow(() -> new NoSuchElementException("Pinned support not found: " + pinnedId));

        // 복제본만 삭제 (원본 삭제 금지)
        if (pinned.isPinnedCopy()) {
            supportRepo.delete(pinned);
        }
    }

    // 좋아요 기능 (userId 기반)
    @Override
    @Transactional
    public LikeResult toggleSupportLike(Long supportId, Long userId) {
        if (supportId == null || userId == null)
            throw new IllegalArgumentException("supportId/userId required");

        JSupport support = supportRepo.findById(supportId)
                .orElseThrow(() -> new NoSuchElementException("Support not found: " + supportId));

        JUser user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));

        var existing = supportLikeRepository.findBySupport_SupportIdAndUser_UserId(supportId, userId);
        boolean nowLiked;

        if (existing.isPresent()) {
            // 이미 좋아요 되어 있으면 삭제
            supportLikeRepository.delete(existing.get());
            nowLiked = false;
        } else {
            // 처음 좋아요 누름
            JSupportLike like = JSupportLike.builder()
                    .support(support)
                    .user(user)
                    .build();
            supportLikeRepository.save(like);
            nowLiked = true;
        }
        long cnt = supportLikeRepository.countBySupport_SupportId(supportId);
        support.setLikeCount((int) cnt);

        return new LikeResult((int) cnt, nowLiked);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSupportLikedByUser(Long supportId, Long userId) {
        if (supportId == null || userId == null) return false;
        return supportLikeRepository.existsBySupport_SupportIdAndUser_UserId(supportId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getSupportLikeCount(Long supportId) {
        if (supportId == null) return 0;
        long cnt = supportLikeRepository.countBySupport_SupportId(supportId);
        return (int) cnt;
    }
}