package org.vaadin.bugrap.domain.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vaadin.bugrap.domain.entities.Reporter;

public interface ReporterRepository extends JpaRepository<Reporter, Long> {
    org.vaadin.bugrap.domain.entities.Reporter getByNameAndPassword(String name, String password);
    org.vaadin.bugrap.domain.entities.Reporter getByNameOrEmail(String name, String email);
}
