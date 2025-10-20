package code.project.repository;

import code.project.domain.Post;
import code.project.repository.search.PostSearch;
import code.project.service.PostService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostSearch {
    List<Post> findByBoard_BoardId(Long boardId);
    List<Post> findByUser_UserId(Long userId);
}
