package org.vaadin.bugrap.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBToolsTest extends TestBase {

    @Test
    public void clear() {
        DBTools.clear();
    }

    @Test
    public void populateDb() {
        DBTools.clear();
        DBTools.create();
        assertEquals(5, new BugrapRepository().findProjects().size());
    }
}
