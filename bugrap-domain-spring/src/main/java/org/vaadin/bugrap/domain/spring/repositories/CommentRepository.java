package org.vaadin.bugrap.domain.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Report;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Long countByReport(Report report);
    Long countByReportAndType(Report report, Comment.Type type);
    List<Comment> findAllByReport(Report report);
    List<Comment> findAllByReportOrderByTimestampAsc(Report report);
    List<Comment> findAllByReportOrderByTimestampDesc(Report report);
}
