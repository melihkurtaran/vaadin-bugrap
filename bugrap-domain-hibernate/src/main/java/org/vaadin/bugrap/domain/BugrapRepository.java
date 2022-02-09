package org.vaadin.bugrap.domain;

import org.vaadin.bugrap.domain.entities.*;

import java.io.Serializable;
import java.util.*;

/**
 * Use this class to access the Bugrap example repository. Simply create using
 * {@code new BugrapRepository()} and call the methods as you see fit.
 */
public class BugrapRepository implements Serializable {

    /**
     * Get a user by username and password
     *
     * @param username
     *            The username of the user
     * @param password
     *            The password of the user
     * @return Returns a user if the username and password match, else NULL
     */
    public Reporter authenticate(String username, String password) {
        final Reporter user = getUser(username);
        if (user == null) {
            throw new RuntimeException("No user " + username);
        }
        if (!user.verifyPassword(password)) {
            throw new RuntimeException("User " + username + ": password doesn't match");
        }
        return user;
    }

    public long countOpenedReports(Project project) {
        return countAssignedReports(project, ReportStatus.OPEN);
    }

    public long countOpenedReports(ProjectVersion projectVersion) {
        return countAssignedReports(projectVersion, ReportStatus.OPEN);
    }

    public long countClosedReports(Project project) {
        return countAssignedReports(project, ReportStatus.CLOSED);
    }

    public long countClosedReports(ProjectVersion projectVersion) {
        return countAssignedReports(
                projectVersion, ReportStatus.CLOSED);
    }

    public long countUnassignedReports(Project project) {
        return countUnassignedReports(project);
    }

    public long countUnassignedReports(ProjectVersion projectVersion) {
        return countReports(
                null, projectVersion, null, AssignmentStatus.UNASSIGNED, null, ReportStatus.ALL);
    }

    /**
     * Get all projects.
     *
     * @return Returns all projects
     */
    public List<Project> findProjects() {
        return new ArrayList<>(JpaDao.getInstance().list(Project.class));
    }

    /**
     * Get all users
     *
     * @return Returns a list of all users
     */
    public Set<Reporter> findReporters() {
        return new LinkedHashSet<>(JpaDao.getInstance().list(Reporter.class));
    }

    public List<Report> findReports(ReportsQuery query) {
        return findReports(
                query.project, query.projectVersion, query.reportStatuses,
                query.reportAssignee);
    }

    /**
     * If the database is empty, populates it with test data. Otherwise, does
     * nothing.
     *
     * @return true, if the database was empty; false, otherwise
     */
    public boolean populateWithTestData() {
        if (isDbEmpty()) {
            DBTools.create();
            return true;
        }
        return false;
    }

    public boolean isDbEmpty() {
        return findReporters().size() == 0;
    }

    public Report save(Report report) {
        report.setTimestamp(new Date());
        return JpaDao.getInstance().store(report);
    }

    public static final class ReportsQuery implements Serializable {
        public Project project;
        public ProjectVersion projectVersion;
        public Set<Report.Status> reportStatuses;
        public Reporter reportAssignee;
    }

    public Comment save(Comment comment) {
        return JpaDao.getInstance().store(comment);
    }

    public void delete(Comment comment) {
        JpaDao.getInstance().delete(comment);
    }

    /**
     * Get all project versions
     *
     * @return Returns a list of all project versions
     */
    public List<ProjectVersion> getVersions() {
        return JpaDao.getInstance().list(ProjectVersion.class);
    }

    public long countAssignedReports(Project project, ReportStatus reportStatus) {
        return countReports(
                project, null, null, AssignmentStatus.ASSIGNED, null, reportStatus);
    }

    /**
     * Counts ASSIGNED reports in a certain projectVersion and reportStatus
     *
     * @param version
     *            The project projectVersion the reports are in
     * @param reportStatus
     *            If the included reports are open or closed
     * @return The number of reports matching that criteria
     */
    public long countAssignedReports(ProjectVersion version, ReportStatus reportStatus) {
        return countReports(
                null, version, null, AssignmentStatus.ASSIGNED, null, reportStatus);
    }

    /**
     * Counts report for selected project, version, reporter and with specified
     * assignment or status
     *
     * @param project
     *            The project, for which the reports should be counted, null for
     *            all projects
     * @param version
     *            The project version, for which these reports are, null for all
     *            versions
     * @param author
     *            Report author, null for all reports
     * @param assigned
     *            Which reports count: Assignment.ASSIGNED - only the assigned
     *            ones, Assignment.UNASSIGNED - (obviously) unassigned,
     *            Assignment.ALL - both
     * @param assignedto
     *            Filter by assigned person (when
     *            assigned==Assignment.ASSIGNED). Might be null to select all
     *            assigned reports
     * @param reportStatus
     *            Status filter: Status.CLOSED - count closed reports,
     *            Status.OPEN - open ones, Status.ALL - all of them
     * @return The number of reports matching that criteria
     */
    private long countReports(Project project, ProjectVersion version,
                              Reporter author, AssignmentStatus assigned, Reporter assignedto,
                              ReportStatus reportStatus) {
        Map<String, Object> params = new HashMap<String, Object>();

        StringBuilder query = new StringBuilder(
                "SELECT COUNT(r) FROM Report r WHERE ");
        if (project != null) {
            params.put("proj", project);
            query.append("r.project=:proj AND ");
        }
        if (version != null) {
            params.put("pv", version);
            query.append("r.version=:pv AND ");
        }
        if (author != null) {
            params.put("auth", author);
            query.append("r.author=:auth AND ");
        }
        if (assigned == AssignmentStatus.UNASSIGNED) {
            query.append("r.assigned IS NULL AND ");
        } else if (assigned == AssignmentStatus.ASSIGNED) {
            if (assignedto != null) {
                params.put("ass", assignedto);
                query.append("r.assigned == :ass AND ");
            } else {
                query.append("r.assigned IS NOT NULL AND ");
            }
        }
        if (reportStatus == ReportStatus.OPEN) {
            params.put("stat", Report.Status.OPEN);
            query.append("(r.status IS NULL OR r.status = :stat)");
        } else if (reportStatus == ReportStatus.CLOSED) {
            params.put("stat", Report.Status.OPEN);
            query.append("(r.status IS NOT NULL AND r.status <> :stat)");
        } else {
            // FIXME The reason it's here is that the query could end with AND
            // clause
            query.append("(1=1)");
        }

        return JpaDao.getInstance().count(query.toString(), params);

    }

    public long countReports(Project project) {
        return countReports(
                project, null, null, AssignmentStatus.ALL, null, ReportStatus.ALL);
    }

    public long countReports(ProjectVersion version) {
        return countReports(
                null, version, null, AssignmentStatus.ALL, null, ReportStatus.ALL);
    }

    /**
     * Counts ASSIGNED reports in a certain project, projectVersion and
     * reportStatus
     *
     * @param version
     *            The project projectVersion the reports are in
     * @param reportStatus
     *            The reportStatus the reports have
     * @return The number of reports matching that criteria
     */
    public long countAssignedReports(Project project,
                                            ProjectVersion version, ReportStatus reportStatus) {
        return countReports(
                project, version, null, AssignmentStatus.ASSIGNED, null, reportStatus);
    }

    /**
     * Get all reports for a project which has not been assigned to a
     * projectVersion
     *
     * @param project
     *            The project to search for
     * @return Returns a list of reports which have not been assigned to a
     *         projectVersion
     */
    public List<Report> getVersionUnassignedReports(Project project) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("proj", project);
        List<Report> unassigned = JpaDao.getInstance().list(
                "SELECT r FROM Report r WHERE r.project = :proj AND r.version IS NULL",
                params);
        return unassigned;
    }

    /**
     * Get all reports for a projectVersion
     *
     * @param version
     *            The project projectVersion to search for
     * @return Returns a list of reports which belong to a projectVersion
     */
    public List<Report> getReportsForVersion(ProjectVersion version) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("proj", version.getProject());
        params.put("ver", version);
        List<Report> reports = JpaDao.getInstance().list(
                "SELECT r FROM Report r WHERE r.project = :proj AND r.version = :ver",
                params);
        return reports;
    }

    /**
     * Get a user by username.
     *
     * @param username
     *            The username of the user
     * @return Returns a user if the username match, else NULL
     */
    public Reporter getUser(String username) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user", username);

        Reporter result = JpaDao.getInstance().find(
                "SELECT r FROM Reporter r WHERE r.name = :user",
                params);

        return result;
    }

    /**
     * Get the projects which have not been closed
     *
     * @return A list of projects which has not been closed
     */
    public List<Project> getActiveProjects() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("closed", false);
        List<Project> result = JpaDao.getInstance().list(
                "SELECT DISTINCT p FROM ProjectVersion as pv JOIN pv.project as p WHERE pv.closed=:closed",
                params);
        return result;
    }

    /**
     * Get project by id
     *
     * @param id
     *            The id of the project
     * @return Returns the project with the corresponding id or NULL if not
     *         found
     */
    public Project getProject(long id) {
        return JpaDao.getInstance().find(Project.class, id);
    }

    /**
     * Get project projectVersion by id
     *
     * @param id
     *            The id of the projectVersion
     *
     * @return Returns the project projectVersion with the corresponding id or
     *         NULL if not found
     */
    public ProjectVersion getVersion(long id) {
        return JpaDao.getInstance().find(ProjectVersion.class, id);
    }

    /**
     * Get a report by its id
     *
     * @param id
     *            The id of the report
     * @return The report which id has been given or NULL if not found
     */
    public Report getReport(long id) {
        return JpaDao.getInstance().find(Report.class, id);
    }

    /**
     * Get a report bu his id
     *
     * @param id
     *            The id of the reporter
     * @return
     */
    public Reporter getReporter(long id) {
        return JpaDao.getInstance().find(Reporter.class, id);
    }

    /**
     * Get a reporter by either email or username.
     *
     * @param username
     *            The username or NULL if searching by email
     * @param email
     *            The email or NULL if search by username
     * @return A reporter which matches the criteria or NULL if not found
     */
    public Reporter getReporterByNameOrEmail(String username,
                                                    String email) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", email);
        params.put("name", username);
        Reporter result = JpaDao.getInstance().find(
                "SELECT r FROM Reporter r WHERE r.email = :email or r.name = :name",
                params);
        return result;
    }

    /**
     * Get versions for a project
     *
     * @param project
     *            The project to get versions for
     * @return
     */
    public List<ProjectVersion> getProjectVersions(Project project) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("proj", project);
        List<ProjectVersion> result = JpaDao.getInstance().list(
                "SELECT pv FROM ProjectVersion pv WHERE pv.project=:proj",
                params);
        return result;
    }

    /**
     * Gets the n latest reports
     *
     * @param amount
     *            The amount of reports to get
     * @return Returns the n latest reports
     */
    public  List<Report> getLatestReports(int amount) {
        List<Report> result = JpaDao.getInstance().list(
                "SELECT r FROM Report r ORDER BY r.timestamp DESC", null,
                amount);
        return result;
    }

    public  List<Report> getLatestReports(Project project,
                                                ProjectVersion version, int amount) {
        Objects.requireNonNull(project);
        Map<String, Object> params = new HashMap<String, Object>();
        StringBuilder query = new StringBuilder("SELECT r FROM Report r WHERE ");
        params.put("proj", project);
        query.append("r.project=:proj ");
        if (version != null) {
            params.put("pv", version);
            query.append("AND r.version=:pv ");
        } else {
            query.append("AND r.version IS NULL ");
        }

        query.append("ORDER BY r.timestamp DESC");

        List<Report> result = JpaDao.getInstance().list(
                query.toString(), params, amount);
        return result;
    }

    /**
     * Get the reports assigned to a specific user
     *
     * @param assignedTo
     *            A reporter to who reports are assigned to
     * @return
     */
    public  List<Report> getVersionAssignedReports(Project project,
                                                         ProjectVersion version, Reporter assignedTo) {
        Map<String, Object> params = new HashMap<String, Object>();
        StringBuilder query = new StringBuilder("SELECT r FROM Report r WHERE ");
        if (project != null) {
            params.put("proj", project);
            query.append("r.project=:proj ");
        } else {
            query.append("r.project IS NULL ");
        }
        if (version != null) {
            params.put("pv", version);
            query.append("AND r.version=:pv ");
        } else {
            query.append("AND r.version IS NULL ");
        }
        if (assignedTo != null) {
            params.put("uid", assignedTo);
            query.append("AND r.assigned=:uid ");
        } else {
            query.append("AND r.assigned IS NULL ");
        }
        List<Report> result = JpaDao.getInstance().list(
                query.toString(), params);
        return result;
    }

    public  List<Report> findReports(Project project,
                                     ProjectVersion projectVersion, Set<Report.Status> reportStatuses,
                                     Reporter assignee) {
        return findReports(
                null, project, projectVersion, null, reportStatuses, null,
                assignee);
    }

     List<Report> findReports(String searchTerm, Project project,
                              ProjectVersion version, Report.Type type,
                              Set<Report.Status> statuses, Report.Priority priority,
                              Reporter assignedTo) {
        Map<String, Object> params = new HashMap<String, Object>();
        StringBuilder query = new StringBuilder(
                "SELECT r FROM Report r WHERE (lower(r.summary) LIKE :term OR lower(r.description) LIKE :term)");

        params.put(
                "term",
                searchTerm == null ? "%" : "%" + searchTerm.toLowerCase() + "%");

        if (project != null) {
            params.put("proj", project);
            query.append(" AND r.project = :proj");
        }

        if (version != null) {
            params.put("ver", version);
            query.append(" AND r.version = :ver");
        }

        if (type != null) {
            params.put("type", type);
            query.append(" AND r.type = :type");
        }

        if (statuses != null) {
            if (statuses.isEmpty()) {
                return Collections.emptyList();
            }
            params.put("statuses", statuses);
            query.append(" AND r.status IN :statuses");
        }

        if (priority != null) {
            params.put("priority", priority);
            query.append(" AND r.priority = :priority");
        }

        if (assignedTo != null) {
            params.put("uid", assignedTo);
            query.append(" AND r.assigned = :uid");
        }

        List<Report> result = JpaDao.getInstance().list(
                query.toString(), params);
        return result;
    }

    /**
     * Count comments for selected report
     *
     * @param report
     *            Report for which the comments should be counted
     * @return Number of comments (not attachments)
     */
    public  long countComments(Report report) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("rep", report);
        params.put("type", Comment.Type.COMMENT);
        return JpaDao.getInstance().count(
                "SELECT COUNT(c) FROM Comment c WHERE c.report=:rep AND c.type=:type",
                params);

    }

    public  List<Comment> findComments(Report report) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("rep", report);
        return JpaDao.getInstance().list(
                "SELECT c FROM Comment c WHERE c.report=:rep", params);
    }

    /**
     * Get latest comments for selected report
     *
     * @param report
     *            Report for which the comments should be fetched
     * @param amount
     *            Number of comments to return
     * @return Latest comments
     */
    public  List<Comment> findLatestComments(Report report, int amount) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("rep", report);
        return JpaDao.getInstance().list(
                "SELECT c FROM Comment c WHERE c.report=:rep ORDER BY c.timestamp DESC",
                params, amount);
    }
}
