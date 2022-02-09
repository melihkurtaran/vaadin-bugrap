package org.vaadin.bugrap.domain.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.bugrap.domain.entities.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
