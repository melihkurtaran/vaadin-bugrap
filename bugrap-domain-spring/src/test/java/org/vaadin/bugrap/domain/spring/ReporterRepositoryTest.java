package org.vaadin.bugrap.domain.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.bugrap.domain.spring.repositories.ReporterRepository;

public class ReporterRepositoryTest extends AbstractTest {
    @Autowired
    private ReporterRepository repo;

    @Test
    public void smoke() {
        System.out.println(repo.findAll());
    }
}
