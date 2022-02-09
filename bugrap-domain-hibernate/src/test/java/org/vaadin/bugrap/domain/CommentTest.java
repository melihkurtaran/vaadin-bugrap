package org.vaadin.bugrap.domain;

import org.junit.jupiter.api.Test;
import org.vaadin.bugrap.domain.entities.Comment;
import org.vaadin.bugrap.domain.entities.Reporter;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class CommentTest extends TestBase {

    @Test
    public void testAuthor() {
        Reporter r = new Reporter();
        r = JpaDao.getInstance().store(r);

        Comment c = new Comment();
        c.setReport(createValidReport());
        c.setAuthor(r);
        c = JpaDao.getInstance().store(c);
        assertEquals(r, c.getAuthor());
    }

    @Test
    public void testComment() {
        Comment c = new Comment();
        c.setComment("comment");
        c.setReport(createValidReport());
        c = JpaDao.getInstance().store(c);
        assertEquals("comment", c.getComment());
    }

    @Test
    public void testTimestamp() {
        Comment c = new Comment();
        Date d = new Date();
        c.setTimestamp(d);
        c.setReport(createValidReport());
        c = JpaDao.getInstance().store(c);
        assertEquals(d, c.getTimestamp());
    }

    @Test
    public void testAttachment() {
        Comment c = new Comment();
        byte[] a = "Test".getBytes();
        c.setAttachment(a);
        c.setReport(createValidReport());
        c = JpaDao.getInstance().store(c);
        assertArrayEquals(a, c.getAttachment());
    }

    @Test
    public void testType() {
        Comment c = new Comment();
        c.setType(Comment.Type.ATTACHMENT);
        c.setReport(createValidReport());
        c = JpaDao.getInstance().store(c);
        assertEquals(Comment.Type.ATTACHMENT, c.getType());
    }

    @Test
    public void testAttachmentName() {
        Comment c = new Comment();
        c.setAttachmentName("aaaa");
        c.setReport(createValidReport());
        c = JpaDao.getInstance().store(c);
        assertEquals("aaaa", c.getAttachmentName());
    }

}
