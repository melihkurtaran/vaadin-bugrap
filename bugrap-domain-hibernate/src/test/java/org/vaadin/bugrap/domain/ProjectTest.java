package org.vaadin.bugrap.domain;

import org.junit.jupiter.api.Test;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.Reporter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectTest extends TestBase {

    @Test
    public void testName() {
        Project p = new Project();
        p.setName("Name");
        p = JpaDao.getInstance().store(p);
        assertEquals("Name", p.getName());
    }

    @Test
    public void testManager() {
        Reporter manager = new Reporter();
        manager = JpaDao.getInstance().store(manager);

        Project p = new Project();
        p.setManager(manager);
        p = JpaDao.getInstance().store(p);
        assertEquals(manager, p.getManager());
    }
}
