package org.vaadin.bugrap.domain.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.bugrap.domain.spring.repositories.ProjectVersionRepository;

public class ProjectVersionRepositoryTest extends AbstractTest {
    @Autowired
    private ProjectVersionRepository repo;

    @Test
    public void smoke() {
        System.out.println(repo.findAll());
    }
}
