package org.vaadin.bugrap.domain;

import org.junit.jupiter.api.BeforeEach;
import org.vaadin.bugrap.domain.entities.*;

import java.util.Date;

public abstract class TestBase  {

    protected Project project;

    protected void clearDb() {
        DBTools.clear();
    }

    @BeforeEach
    public void setUp() {
        // Clear the database
        clearDb();

        project = new Project();
        project.setName("Dummy project");
        project = JpaDao.getInstance().store(project);
    }

    protected Report createValidReport() {
        Report newReport = new Report();
        newReport.setPriority(Report.Priority.TRIVIAL);
        newReport.setType(Report.Type.FEATURE);
        newReport.setProject(project);
        return new BugrapRepository().save(newReport);
    }

    protected Reporter createReporter() {
        final Reporter reporter = new Reporter();
        reporter.setName("Foo");
        return JpaDao.getInstance().store(reporter);
    }
}
