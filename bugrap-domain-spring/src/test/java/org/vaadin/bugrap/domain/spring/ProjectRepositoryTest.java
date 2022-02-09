package org.vaadin.bugrap.domain.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.bugrap.domain.spring.repositories.ProjectRepository;

public class ProjectRepositoryTest extends AbstractTest{
    @Autowired
    private ProjectRepository repo;

    @Test
    public void smoke() {
        System.out.println(repo.findAll());
    }
}
