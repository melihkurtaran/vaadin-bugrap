package org.vaadin.bugrap.domain;

import org.junit.jupiter.api.Test;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectVersionTest extends TestBase {

    @Test
    public void testVersion() {
        ProjectVersion pv = new ProjectVersion();
        pv.setVersion("Version");
        pv = JpaDao.getInstance().store(pv);
        assertEquals("Version", pv.getVersion());
    }

    @Test
    public void testReleaseDate() {
        ProjectVersion pv = new ProjectVersion();
        Date d = new Date();
        pv.setReleaseDate(d);
        pv = JpaDao.getInstance().store(pv);
        assertEquals(d, pv.getReleaseDate());
    }

    @Test
    public void testClosed() {
        ProjectVersion pv = new ProjectVersion();
        pv.setClosed(true);
        pv = JpaDao.getInstance().store(pv);
        assertTrue(pv.isClosed());

        pv.setClosed(false);
        pv = JpaDao.getInstance().store(pv);
        assertFalse(pv.isClosed());
    }

    @Test
    public void testProject() {
        Project p = new Project();
        p = JpaDao.getInstance().store(p);

        ProjectVersion pv = new ProjectVersion();
        pv.setProject(p);
        pv = JpaDao.getInstance().store(pv);

        assertEquals(p, pv.getProject());
    }
}
