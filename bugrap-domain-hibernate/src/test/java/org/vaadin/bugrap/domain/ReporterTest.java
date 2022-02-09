/**
 *
 */
package org.vaadin.bugrap.domain;

import org.junit.jupiter.api.Test;
import org.vaadin.bugrap.domain.entities.Reporter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author John Ahlroos / Vaadin Oy
 */
public class ReporterTest extends TestBase {

    @Test
    public void testName() {
        Reporter reporter = new Reporter();
        reporter.setName("Test User");
        reporter = JpaDao.getInstance().store(reporter);
        String name = reporter.getName();
        assertEquals("Test User", name);
    }

    @Test
    public void testEmail() {
        Reporter reporter = new Reporter();
        reporter.setEmail("test@test.com");
        reporter = JpaDao.getInstance().store(reporter);
        String email = reporter.getEmail();
        assertEquals("test@test.com", email);
    }

    @Test
    public void testPassword() {
        Reporter reporter = new Reporter();
        reporter.hashPassword("abcdefgh");
        reporter = JpaDao.getInstance().store(reporter);
        assertTrue(reporter.verifyPassword("abcdefgh"));
    }

    @Test
    public void testAuth() {
        Reporter reporter = new Reporter();
        reporter.setName("Foo");
        reporter.hashPassword("abcdefgh");
        JpaDao.getInstance().store(reporter);
        new BugrapRepository().authenticate("Foo", "abcdefgh");
    }

    @Test
    public void testAdmin() {
        Reporter reporter = new Reporter();
        reporter.setAdmin(true);
        reporter = JpaDao.getInstance().store(reporter);
        assertTrue(reporter.isAdmin());

        reporter.setAdmin(false);
        reporter = JpaDao.getInstance().store(reporter);
        assertFalse(reporter.isAdmin());
    }
}
