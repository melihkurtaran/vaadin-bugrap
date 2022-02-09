package org.vaadin.bugrap.domain.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.bugrap.domain.spring.repositories.ReportRepository;

public class ReportRepositoryTest extends AbstractTest {
    @Autowired
    private ReportRepository repo;

    @Test
    public void smoke() {
        System.out.println(repo.findAll());
    }
}
