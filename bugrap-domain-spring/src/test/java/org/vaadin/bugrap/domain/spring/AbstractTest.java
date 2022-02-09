package org.vaadin.bugrap.domain.spring;

import org.h2.tools.Server;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { BugrapDomainConfiguration.class })
public abstract class AbstractTest {
    @Autowired
    private DBTools dbTools;

    @BeforeEach
    public void clear() {
        dbTools.clear();
    }

    // allows for remote access via jdbc:h2:tcp://localhost/mem:testdb
    private static Server h2Server;

    static {
        try {
            h2Server = Server.createTcpServer();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void startH2TcpServer() throws Exception {
        if (!h2Server.isRunning(true)) {
            h2Server.start();
        }
    }
}
