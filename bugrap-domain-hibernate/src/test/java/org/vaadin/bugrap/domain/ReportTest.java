package org.vaadin.bugrap.domain;

import org.junit.jupiter.api.Test;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportTest extends TestBase {

    @Test
    public void testType() {
        Report r = createValidReport();
        r.setType(Report.Type.BUG);
        r = JpaDao.getInstance().store(r);
        assertEquals(Report.Type.BUG, r.getType());
    }

    @Test
    public void testSummary() {
        Report r = createValidReport();
        r.setSummary("sum");
        r = JpaDao.getInstance().store(r);
        assertEquals("sum", r.getSummary());
    }

    @Test
    public void testDescription() {
        Report r = createValidReport();
        r.setDescription("descr");
        r = JpaDao.getInstance().store(r);
        assertEquals("descr", r.getDescription());
    }

    @Test
    public void testProject() {
        Project p = new Project();
        p = JpaDao.getInstance().store(p);

        Report r = createValidReport();
        r.setProject(p);
        r = JpaDao.getInstance().store(r);
        assertEquals(p, r.getProject());
    }

    @Test
    public void testVersion() {
        ProjectVersion v = new ProjectVersion();
        v = JpaDao.getInstance().store(v);

        Report r = createValidReport();
        r.setVersion(v);
        r = JpaDao.getInstance().store(r);
        assertEquals(v, r.getVersion());
    }

    @Test
    public void testGetPriority() {
        Report r = createValidReport();
        r.setPriority(Report.Priority.BLOCKER);
        r = JpaDao.getInstance().store(r);
        assertEquals(Report.Priority.BLOCKER, r.getPriority());
    }

    @Test
    public void testSetPriority() {
        Report r = createValidReport();
        r.setPriority(Report.Priority.BLOCKER);
        r = JpaDao.getInstance().store(r);
        assertEquals(Report.Priority.BLOCKER, r.getPriority());
    }

    @Test
    public void testAssigned() {
        Reporter u = new Reporter();
        u = JpaDao.getInstance().store(u);

        Report r = createValidReport();
        r.setAssigned(u);
        r = JpaDao.getInstance().store(r);
        assertEquals(u, r.getAssigned());
    }

    @Test
    public void testOccursIn() {
        ProjectVersion pv = new ProjectVersion();
        pv = JpaDao.getInstance().store(pv);

        Report r = createValidReport();
        r.setOccursIn(pv);
        r = JpaDao.getInstance().store(r);
        assertEquals(pv, r.getOccursIn());
    }

    @Test
    public void testTimestamp() {
        Report r = createValidReport();
        Instant d = Instant.now();
        r.setTimestamp(Date.from(d));
        r = JpaDao.getInstance().store(r);
        //close enough
        assertEquals(d.getEpochSecond(), r.getTimestamp().toInstant().getEpochSecond());
    }
}
