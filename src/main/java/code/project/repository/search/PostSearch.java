package code.project.repository.search;

import code.project.domain.Post;
import code.project.dto.PageRequestDTO;
import org.springframework.data.domain.Page;

public interface PostSearch {

    Page<Post> search1(PageRequestDTO pageRequestDTO);
}
