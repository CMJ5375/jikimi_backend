package code.project.service;

import code.project.domain.JSupport;
import code.project.domain.JUser;
import code.project.dto.JSupportDTO;
import code.project.repository.JSupportRepository;
import code.project.repository.JUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JSupportServiceImpl implements JSupportService {

    private final JSupportRepository supportRepo;
    private final JUserRepository userRepo;

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
                .createdAt(e.getCreatedAt())
                .userId(e.getJUser().getUserId())
                .username(e.getJUser().getUsername())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JSupportDTO> list(String type, String keyword, int page, int size) {
        Page<JSupport> result;
        Pageable pageable = PageRequest.of(page, size);

        if (keyword == null || keyword.trim().isEmpty()) {
            result = supportRepo.findByTypeOrderByPinned(type, pageable);
        } else {
            result = supportRepo.searchByTypeAndKeyword(type, keyword.trim(), pageable);
        }
        return result.map(this::toDTO);
    }

    @Override
    public JSupportDTO get(Long id, boolean increaseView) {
        JSupport entity = supportRepo.findById(id).orElseThrow();
        if (increaseView && (entity.getType().equals("NOTICE") || entity.getType().equals("DATAROOM"))) {
            entity.setViewCount(entity.getViewCount() + 1);
        }
        return toDTO(entity);
    }

    @Override
    public Long create(JSupportDTO dto, Long adminId) {
        JUser admin = userRepo.findById(adminId).orElseThrow();

        JSupport entity = JSupport.builder()
                .JUser(admin)
                .type(dto.getType())
                .title(dto.getTitle())
                .content(dto.getContent())
                .fileUrl(dto.getFileUrl())
                .fileName(dto.getFileName())
                .viewCount(0)
                .pinnedCopy(false)
                .originalId(null)
                .build();

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
        supportRepo.deleteById(id);
    }

    @Override
    public void pin(Long id, Long adminId) {
        JSupport origin = supportRepo.findById(id).orElseThrow();
        String type = origin.getType();

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
        JSupport pinned = supportRepo.findById(pinnedId).orElseThrow();
        if (!pinned.isPinnedCopy()) return;
        supportRepo.delete(pinned);
    }
}