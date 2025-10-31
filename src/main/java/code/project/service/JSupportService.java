package code.project.service;

import code.project.dto.JSupportDTO;
import org.springframework.data.domain.Page;

public interface JSupportService {

    // type에 따라 NOTICE / FAQ / DATAROOM 구분
    Page<JSupportDTO> list(String type, String keyword, int page, int size);
    JSupportDTO get(Long id, boolean increaseView);
    Long create(JSupportDTO dto, Long adminId);
    void update(JSupportDTO dto, Long adminId);
    void delete(Long id, Long adminId);
    void pin(Long id, Long adminId);
    void unpin(Long pinnedId, Long adminId);
}
