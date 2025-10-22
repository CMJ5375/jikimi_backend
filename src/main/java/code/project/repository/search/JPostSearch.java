package code.project.repository.search;

import code.project.domain.JPost;
import code.project.dto.PageRequestDTO;
import org.springframework.data.domain.Page;

public interface JPostSearch {

    Page<JPost> search1(PageRequestDTO pageRequestDTO);
}
