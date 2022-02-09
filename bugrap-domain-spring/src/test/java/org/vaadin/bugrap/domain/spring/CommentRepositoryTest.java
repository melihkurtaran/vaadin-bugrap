package org.vaadin.bugrap.domain.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.bugrap.domain.spring.repositories.CommentRepository;

public class CommentRepositoryTest extends AbstractTest {
    @Autowired
    private CommentRepository repo;

    @Test
    public void smoke() {
        System.out.println(repo.findAll());
    }
}
