package org.vaadin.bugrap.domain;

import org.junit.jupiter.api.Test;
import org.vaadin.bugrap.domain.entities.Project;
import org.vaadin.bugrap.domain.entities.ProjectVersion;
import org.vaadin.bugrap.domain.entities.Report;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class FacadeUtilTest extends TestBase {

    @Test
    public void testStore() {
        Reporter r = new Reporter();
        r = JpaDao.getInstance().store(r);

        assertNotSame(-1, r.getId());

        Reporter x = JpaDao.getInstance().find(Reporter.class, r.getId());
        assertEquals(r, x);
    }

    @Test
    public void testGetUnassignedReports() {

        // Add a test project
        Project p = new Project();
        p = JpaDao.getInstance().store(p);

        // Add some unassigned reports
        Report r = createValidReport();
        r.setTimestamp(new Date());
        r.setProject(p);
        r.setSummary("Report1");
        JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setTimestamp(new Date());
        r.setProject(p);
        r.setSummary("Report2");
        JpaDao.getInstance().store(r);

        List<Report> reports = new BugrapRepository().getVersionUnassignedReports(p);
        assertEquals(2, reports.size());
        assertEquals("Report1", reports.get(0).getSummary());
        assertEquals("Report2", reports.get(1).getSummary());

    }

    @Test
    public void testGetReportsForVersion() {

        // Add a test project
        Project p = new Project();
        p = JpaDao.getInstance().store(p);

        // Add a test projectVersion
        ProjectVersion v = new ProjectVersion();
        v.setProject(p);
        v = JpaDao.getInstance().store(v);

        // Add some versioned reports
        Report r = createValidReport();
        r.setTimestamp(new Date());
        r.setProject(p);
        r.setVersion(v);
        r.setSummary("Report1");
        JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setTimestamp(new Date());
        r.setProject(p);
        r.setVersion(v);
        r.setSummary("Report2");
        JpaDao.getInstance().store(r);

        List<Report> versioned = new BugrapRepository().getReportsForVersion(v);
        assertEquals(2, versioned.size());
        assertEquals("Report1", versioned.get(0).getSummary());
        assertEquals("Report2", versioned.get(1).getSummary());
    }

    @Test
    public void testGetUser() throws NoSuchAlgorithmException,
            UnsupportedEncodingException {

        // Add a reporter
        Reporter r = new Reporter();
        r.setName("name");
        r.hashPassword("password");
        r = JpaDao.getInstance().store(r);

        Reporter x = new BugrapRepository().authenticate("name", "password");
        assertEquals(r, x);
    }

    @Test
    public void testGetProjects() {
        clearDb();

        // Add some projects
        Project p = new Project();
        p.setName("Project1");
        JpaDao.getInstance().store(p);
        p = new Project();
        p.setName("Project2");
        JpaDao.getInstance().store(p);
        p = new Project();
        p.setName("Project3");
        JpaDao.getInstance().store(p);

        List<Project> projects = new BugrapRepository().findProjects();
        assertEquals(3, projects.size());
        assertEquals("Project1", projects.get(0).getName());
        assertEquals("Project2", projects.get(1).getName());
        assertEquals("Project3", projects.get(2).getName());
    }

    @Test
    public void testGetActiveProjects() {
        DBTools.clear();

        // Active ( One open project)
        Project p = new Project();
        p.setName("Project1");
        p = JpaDao.getInstance().store(p);

        ProjectVersion v = new ProjectVersion();
        v.setProject(p);
        v.setClosed(false);
        JpaDao.getInstance().store(v);

        // Active ( One open / one closed project)
        p = new Project();
        p.setName("Project2");
        p = JpaDao.getInstance().store(p);

        v = new ProjectVersion();
        v.setProject(p);
        v.setClosed(false);
        JpaDao.getInstance().store(v);

        v = new ProjectVersion();
        v.setProject(p);
        v.setClosed(true);
        JpaDao.getInstance().store(v);

        // Inactive ( No versions)
        p = new Project();
        p.setName("Project3");
        JpaDao.getInstance().store(p);

        // Inactive ( One closed projectVersion)
        p = new Project();
        p.setName("Project4");
        p = JpaDao.getInstance().store(p);

        v = new ProjectVersion();
        v.setProject(p);
        v.setClosed(true);
        JpaDao.getInstance().store(v);

        List<Project> active = new BugrapRepository().getActiveProjects();
        assertEquals(2, active.size());
        assertEquals("Project1", active.get(0).getName());
        assertEquals("Project2", active.get(1).getName());
    }

    @Test
    public void testGetProject() {
        // Add a project to get
        Project p = new Project();
        p = JpaDao.getInstance().store(p);

        Project x = new BugrapRepository().getProject(p.getId());
        assertEquals(p, x);
    }

    @Test
    public void testGetVersion() {
        ProjectVersion v = new ProjectVersion();
        v = JpaDao.getInstance().store(v);

        ProjectVersion x = new BugrapRepository().getVersion(v.getId());
        assertEquals(v, x);
    }

    @Test
    public void testGetReport() {
        Report r = createValidReport();
        r = JpaDao.getInstance().store(r);

        Report x = new BugrapRepository().getReport(r.getId());
        assertEquals(r, x);
    }

    @Test
    public void testGetReporter() {
        Reporter r = new Reporter();
        r = JpaDao.getInstance().store(r);

        Reporter x = new BugrapRepository().getReporter(r.getId());
        assertEquals(r, x);
    }

    @Test
    public void testGetReporterByNameOrEmail() {
        Reporter r = new Reporter();
        r.setName("name");
        r.setEmail("email");
        r = JpaDao.getInstance().store(r);

        // By name
        Reporter x = new BugrapRepository().getReporterByNameOrEmail("name", null);
        assertEquals(r, x);

        // By email
        Reporter y = new BugrapRepository().getReporterByNameOrEmail(null, "email");
        assertEquals(r, y);

        // By both
        Reporter z = new BugrapRepository().getReporterByNameOrEmail("name", "email");
        assertEquals(r, z);
    }

    @Test
    public void testGetReporters() {
        Reporter r = new Reporter();
        r.setName("Reporter1");
        JpaDao.getInstance().store(r);
        r = new Reporter();
        r.setName("Reporter2");
        JpaDao.getInstance().store(r);
        r = new Reporter();
        r.setName("Reporter3");
        JpaDao.getInstance().store(r);

        List<Reporter> reporters = new ArrayList<>(new BugrapRepository().findReporters());
        assertEquals(3, reporters.size());
        assertEquals("Reporter1", reporters.get(0).getName());
        assertEquals("Reporter2", reporters.get(1).getName());
        assertEquals("Reporter3", reporters.get(2).getName());
    }

    @Test
    public void testGetVersions() {
        ProjectVersion pv = new ProjectVersion();
        pv.setVersion("Version1");
        JpaDao.getInstance().store(pv);
        pv = new ProjectVersion();
        pv.setVersion("Version2");
        JpaDao.getInstance().store(pv);
        pv = new ProjectVersion();
        pv.setVersion("Version3");
        JpaDao.getInstance().store(pv);

        List<ProjectVersion> versions = new BugrapRepository().getVersions();
        assertEquals(3, versions.size());
        assertEquals("Version1", versions.get(0).getVersion());
        assertEquals("Version2", versions.get(1).getVersion());
        assertEquals("Version3", versions.get(2).getVersion());
    }

    @Test
    public void testGetLatestReportsInt() {
        Calendar cal = Calendar.getInstance();

        Report r = createValidReport();
        r.setSummary("Report1");
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        r = createValidReport();
        r.setSummary("Report2");
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        r = createValidReport();
        r.setSummary("Report3");
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        r = createValidReport();
        r.setSummary("Report4");
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        List<Report> reports = new BugrapRepository().getLatestReports(2);
        assertEquals(2, reports.size());
        assertEquals("Report4", reports.get(0).getSummary());
        assertEquals("Report3", reports.get(1).getSummary());
    }

    @Test
    public void testGetLatestReportsProjectProjectVersionInt() {
        Project p = new Project();
        p = JpaDao.getInstance().store(p);

        ProjectVersion pv = new ProjectVersion();
        pv = JpaDao.getInstance().store(pv);

        Calendar cal = Calendar.getInstance();

        // Report with only project
        Report r = createValidReport();
        r.setSummary("Report1");
        r.setProject(p);
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        // Report with only projectVersion
        r = createValidReport();
        r.setVersion(pv);
        r.setSummary("Report2");
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        // Report with project and projectVersion
        r = createValidReport();
        r.setProject(p);
        r.setVersion(pv);
        r.setSummary("Report3");
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        // Report with project and projectVersion
        r = createValidReport();
        r.setProject(p);
        r.setVersion(pv);
        r.setSummary("Report4");
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        // Report with project and projectVersion
        r = createValidReport();
        r.setProject(p);
        r.setVersion(pv);
        r.setSummary("Report5");
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        cal.add(Calendar.DAY_OF_MONTH, 1);

        // Report with no project or projectVersion
        r = createValidReport();
        r.setSummary("Report6");
        r.setTimestamp(cal.getTime());
        r = JpaDao.getInstance().store(r);

        List<Report> reports = new BugrapRepository().getLatestReports(p, pv, 2);
        assertEquals(2, reports.size());
        assertEquals("Report5", reports.get(0).getSummary());
        assertEquals("Report4", reports.get(1).getSummary());

        reports = new BugrapRepository().getLatestReports(p, null, 2);
        assertEquals(1, reports.size());
        assertEquals("Report1", reports.get(0).getSummary());
    }

    @Test
    public void testCountReportsProjectVersionReportStatus() {
        ProjectVersion pv = new ProjectVersion();
        pv = JpaDao.getInstance().store(pv);

        Report r = createValidReport();
        r.setVersion(pv);
        r.setStatus(null);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setVersion(pv);
        r.setStatus(null);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setVersion(pv);
        r.setStatus(Report.Status.CANT_FIX);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setVersion(pv);
        r.setStatus(Report.Status.DUPLICATE);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setVersion(pv);
        r.setStatus(Report.Status.FIXED);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setStatus(Report.Status.FIXED);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        long open = new BugrapRepository().countAssignedReports(pv, ReportStatus.OPEN);
        assertEquals(2, open);

        long closed = new BugrapRepository().countAssignedReports(pv, ReportStatus.CLOSED);
        assertEquals(3, closed);
    }

    @Test
    public void testCountReportsProjectProjectVersionReportStatus() {
        Project p = new Project();
        p = JpaDao.getInstance().store(p);

        ProjectVersion pv = new ProjectVersion();
        pv.setProject(p);
        pv = JpaDao.getInstance().store(pv);

        Report r = createValidReport();
        r.setStatus(null);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setProject(p);
        r.setStatus(null);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setVersion(pv);
        r.setStatus(null);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setProject(p);
        r.setVersion(pv);
        r.setStatus(null);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setStatus(Report.Status.INVALID);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setProject(p);
        r.setStatus(Report.Status.NEED_MORE_INFO);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setVersion(pv);
        r.setStatus(Report.Status.WONT_FIX);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setProject(p);
        r.setVersion(pv);
        r.setStatus(Report.Status.WORKS_FOR_ME);
        r.setAssigned(createReporter());
        r = JpaDao.getInstance().store(r);

        long open = new BugrapRepository().countAssignedReports(null, null, ReportStatus.OPEN);
        assertEquals(4, open);
        open = new BugrapRepository().countAssignedReports(p, null, ReportStatus.OPEN);
        assertEquals(2, open);
        open = new BugrapRepository().countAssignedReports(null, pv, ReportStatus.OPEN);
        assertEquals(2, open);
        open = new BugrapRepository().countAssignedReports(p, pv, ReportStatus.OPEN);
        assertEquals(1, open);

        long closed = new BugrapRepository().countAssignedReports(
                null, null, ReportStatus.CLOSED);
        assertEquals(4, closed);
        closed = new BugrapRepository().countAssignedReports(p, null, ReportStatus.CLOSED);
        assertEquals(2, closed);
        closed = new BugrapRepository().countAssignedReports(null, pv, ReportStatus.CLOSED);
        assertEquals(2, closed);
        closed = new BugrapRepository().countAssignedReports(p, pv, ReportStatus.CLOSED);
        assertEquals(1, closed);
    }

    @Test
    public void testGetAssignedReports() {
        Project p = new Project();
        p = JpaDao.getInstance().store(p);

        ProjectVersion pv = new ProjectVersion();
        pv = JpaDao.getInstance().store(pv);

        Reporter u = new Reporter();
        u = JpaDao.getInstance().store(u);

        Report r = createValidReport();
        r.setProject(p);
        r.setVersion(pv);
        r.setAssigned(u);
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setProject(p);
        r.setVersion(pv);
        r.setAssigned(u);
        r = JpaDao.getInstance().store(r);

        r = createValidReport();
        r.setProject(p);
        r.setVersion(pv);
        r = JpaDao.getInstance().store(r);

        List<Report> assigned = new BugrapRepository().getVersionAssignedReports(p, pv, u);
        assertEquals(2, assigned.size());
    }

    @Test
    public void testSearchReports() {
        Project project = new Project();
        project = JpaDao.getInstance().store(project);

        ProjectVersion version = new ProjectVersion();
        version = JpaDao.getInstance().store(version);

        // Has summary
        Report r = createValidReport();
        r.setSummary("Report1");
        r.setProject(project);
        r = JpaDao.getInstance().store(r);

        // Has description
        r = createValidReport();
        r.setVersion(version);
        r.setDescription("Report2");
        r = JpaDao.getInstance().store(r);

        List<Report> result = new BugrapRepository().findReports(
                "Report1", null, null, null, null, null, null);
        assertEquals(1, result.size());

        result = new BugrapRepository().findReports(
                "Report2", null, null, null, null, null, null);
        assertEquals(1, result.size());

        result = new BugrapRepository().findReports(
                "report", null, null, null, null, null, null);
        assertEquals(2, result.size());

        result = new BugrapRepository().findReports(
                null, project, null, null, null, null, null);
        assertEquals(1, result.size());

        result = new BugrapRepository().findReports(
                null, null, version, null, null, null, null);
        assertEquals(1, result.size());

        // TODO Could be expanded with a million more cases
    }
}
