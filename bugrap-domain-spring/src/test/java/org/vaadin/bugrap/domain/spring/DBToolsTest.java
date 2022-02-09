package org.vaadin.bugrap.domain.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.bugrap.domain.spring.repositories.ProjectRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBToolsTest extends AbstractTest {
    @Autowired
    private DBTools dbTools;
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void clear() {
        dbTools.clear();
    }

    @Test
    public void populateDb() {
        dbTools.create();
        assertEquals(5, projectRepository.findAll().size());
    }
}
