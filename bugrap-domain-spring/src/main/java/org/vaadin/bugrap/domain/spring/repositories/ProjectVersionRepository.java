package org.vaadin.bugrap.domain.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.List;

public interface ProjectVersionRepository extends JpaRepository<ProjectVersion, Long> {
    List<ProjectVersion> findAllByProject(Project p);
}
