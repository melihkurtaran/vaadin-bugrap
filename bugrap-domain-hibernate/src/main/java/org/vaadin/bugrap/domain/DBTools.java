package org.vaadin.bugrap.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.bugrap.domain.entities.*;

import java.util.*;


public class DBTools {

    private static final Logger log = LoggerFactory.getLogger(DBTools.class);

    /**
     * Removes all entities from the database but won't drop the tables.
     */
    public static void clear() {
        // Comments
        log.info("* Removing all comments... ");
        JpaDao.getInstance().deleteAll(Comment.class);

        // Reports
        log.info("* Removing all reports... ");
        JpaDao.getInstance().deleteAll(Report.class);

        // Report versions
        log.info("* Removing all project versions... ");
        JpaDao.getInstance().deleteAll(ProjectVersion.class);

        // Projects
        log.info("* Removing all projects... ");
        JpaDao.getInstance().deleteAll(Project.class);

        // Reporters
        log.info("* Removing all users... ");
        JpaDao.getInstance().deleteAll(Reporter.class);
    }

    private static Reporter createUser(String name, String email,
            String password, boolean admin) {
        Reporter user = new Reporter();
        user.setAdmin(admin);
        user.setName(name);
        user.setEmail(email);
        try {
            user.hashPassword(password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return JpaDao.getInstance().store(user);
    }

    public static void create() {
        final BugrapRepository repo = new BugrapRepository();

        log.info("===== CREATING DATABASE ========");

        log.info("* Creating admin users... ");
        Reporter admin = createUser("admin", "admin@bugrap.com", "admin", true);
        Reporter manager = createUser(
                "manager", "manager@bugrap.com", "manager", false);
        Reporter developer = createUser(
                "developer", "developer@bugrap.com", "developer", false);
        log.info("Done.");

        log.info("* Creating projects and versions... ");
        for (int p = 0; p < 5; p++) {
            Project project = new Project();
            project.setManager(manager);
            project.setDevelopers(Collections.singletonList(developer));
            project.setName("Project " + (p + 1));
            project = JpaDao.getInstance().store(project);

            for (int v = 0; v < 3; v++) {
                ProjectVersion version = new ProjectVersion();
                version.setVersion("Version " + (v + 1));
                version.setProject(project);
                JpaDao.getInstance().store(version);
            }
        }

        log.info("Done.");

        log.info("* Creating reports... ");
        List<Comment> bugcomments = new ArrayList<Comment>();
        Random random = new Random(new Date().getTime());
        List<Project> projects = repo.findProjects();
        for (int r = 0; r < 100; r++) {
            Project p = projects.get(r % 4);
            List<ProjectVersion> versions = repo.getProjectVersions(p);

            Report report = new Report();
            report.setReportedTimestamp(new Date());
            report.setProject(p);
            report.setOccursIn(versions.get(r % 2));

            if (random.nextBoolean()) {
                report.setVersion(repo.getProjectVersions(p).get(r % 2));
            }
            if (random.nextBoolean()) {
                report.setAssigned(developer);
            }

            Report.Priority[] priorities = Report.Priority.values();
            report.setPriority(priorities[r % priorities.length]);

            Report.Type type = random.nextBoolean()
                    ? Report.Type.BUG : Report.Type.FEATURE;
            report.setType(type);

            if (random.nextBoolean()) {
                report.setStatus(Report.Status.values()[r % 6]);
            }

            if (type == Report.Type.BUG) {
                report.setSummary("Bug report " + random.nextInt(100));
            } else {
                report.setSummary("Feature request " + random.nextInt(100));
            }

            report.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
                                  + "Sed posuere interdum sem. Quisque ligula eros ullamcorper quis, lacinia "
                                  + "quis facilisis sed sapien. Mauris varius diam vitae arcu. Sed arcu lectus "
                                  + "auctor vitae, consectetuer et venenatis eget velit. Sed augue orci, "
                                  + "lacinia eu tincidunt et eleifend nec lacus. Donec ultricies nisl ut felis,"
                                  + " suspendisse potenti. Lorem ipsum ligula ut hendrerit mollis, ipsum erat vehicula "
                                  + "risus, eu suscipit sem libero nec erat. Aliquam erat volutpat. "
                                  + "Sed congue augue vitae neque. Nulla consectetuer porttitor pede. "
                                  + "Fusce purus morbi tortor magna condimentum vel, placerat id blandit sit "
                                  + "amet tortor.\n\nMauris sed libero. Suspendisse facilisis nulla in lacinia laoreet,"
                                  + " lorem velit accumsan velit vel mattis libero nisl et sem. Proin interdum maecenas"
                                  + " massa turpis sagittis in, interdum non lobortis vitae massa. Quisque purus lectus, "
                                  + "posuere eget imperdiet nec sodales id arcu. Vestibulum elit pede dictum eu, viverra "
                                  + "non tincidunt eu ligula.\n\nNam molestie nec tortor. Donec placerat leo sit amet velit."
                                  + " Vestibulum id justo ut vitae massa. Proin in dolor mauris consequat aliquam. "
                                  + "Donec ipsum, vestibulum ullamcorper venenatis augue. Aliquam tempus nisi in auctor "
                                  + "vulputate, erat felis pellentesque augue nec, pellentesque lectus justo nec erat. "
                                  + "Aliquam et nisl. Quisque sit amet dolor in justo pretium condimentum.");

            report = repo.save(report);

            while (random.nextBoolean()) {
                Comment comment = new Comment();
                comment.setAuthor(developer);
                comment.setTimestamp(new Date());
                comment.setReport(report);

                if (random.nextBoolean()) {
                    comment.setType(Comment.Type.ATTACHMENT);
                    comment.setComment("Some attachment...");
                    comment.setAttachmentName("text.txt");
                    comment.setAttachment("This is some text attachement content".getBytes());
                } else {
                    // FIX The entity could be either comment or attachment
                    comment.setComment("Some comment...");
                    comment.setType(Comment.Type.COMMENT);
                }
                // FIX Should store them in database after all reports are
                // created
                bugcomments.add(comment);
            }
        }

        // FIX Store comments in DB
        JpaDao.getInstance().storeAll(bugcomments);

        log.info("Done.");
    }
}
