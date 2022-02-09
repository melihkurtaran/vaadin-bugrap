package org.vaadin.bugrap.domain.entities;

import java.util.Date;

import javax.persistence.*;

@Entity
public class Comment extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    public enum Type {
        COMMENT, ATTACHMENT
    }

    @ManyToOne private Reporter author;

    @Column(name = "COMMENT", columnDefinition = "LONGVARCHAR") private String comment;

    @Enumerated private Type type;

    @Basic(fetch = FetchType.LAZY) @Lob private byte[] attachment;

    private String attachmentName;

    @Temporal(TemporalType.TIMESTAMP) private Date timestamp;

    @ManyToOne private Report report;

    public Reporter getAuthor() {
        return author;
    }

    public void setAuthor(Reporter author) {
        this.author = author;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setAttachment(byte[] attachment) {
        this.attachment = attachment;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    @PrePersist
    void updateDates() {
        if (timestamp == null) {
            timestamp = new Date();
        }
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}
