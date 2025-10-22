package code.project.repository.search;

import code.project.domain.JPost;
import code.project.domain.QJPost;
import code.project.dto.PageRequestDTO;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Objects;

@Slf4j
public class JPostSearchImpl extends QuerydslRepositorySupport implements JPostSearch {

    public JPostSearchImpl() {
        super(JPost.class);
    }

    @Override
    public Page<JPost> search1(PageRequestDTO pageRequestDTO) {
        log.info("PostSearchImpl.search1() called: {}", pageRequestDTO);

        QJPost post = QJPost.jPost;

        // 기본 Query
        JPQLQuery<JPost> query = from(post);

        // (필요하면 여기서 검색조건 추가)
        // if (pageRequestDTO.getKeyword() != null) {
        //     String kw = pageRequestDTO.getKeyword();
        //     query.where(post.title.containsIgnoreCase(kw)
        //          .or(post.content.containsIgnoreCase(kw)));
        // }

        // 페이징/정렬
        PageRequest pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("postId").descending() // 정렬 컬럼: 엔티티 필드명
        );

        Objects.requireNonNull(getQuerydsl()).applyPagination(pageable, query);

        // 실행
        List<JPost> list = query.fetch();
        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }
}