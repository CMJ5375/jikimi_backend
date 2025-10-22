package code.project.repository;

import code.project.domain.JNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JNoticeRepository extends JpaRepository<JNotice, Long> {}
