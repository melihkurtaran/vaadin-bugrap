package org.vaadin.bugrap.domain.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Report extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    public enum Priority {
        TRIVIAL, MINOR, NORMAL, MAJOR, CRITICAL, BLOCKER;
    }

    public enum Status {
        OPEN("Open"),
        FIXED("Fixed"),
        INVALID("Invalid"),
        WONT_FIX("Won't fix"),
        CANT_FIX("Can't fix"),
        DUPLICATE("Duplicate"),
        WORKS_FOR_ME("Works for me"),
        NEED_MORE_INFO("Needs more information");

        private final String programmerFriendly;

        private Status(String programmerFriendly) {
            this.programmerFriendly = programmerFriendly;
        }

        /**
         * Returns the programmer-friendly string form of this report status.
         */
        @Override
        public String toString() {
            return programmerFriendly;
        }
    }

    public enum Type {
        BUG, FEATURE
    }

    @Column(nullable = false) @Enumerated private Type type;
    @Enumerated private Status status;

    @Column(name = "SUMMARY", columnDefinition = "LONGVARCHAR") private String summary;

    @Column(name = "DESCRIPTION", columnDefinition = "LONGVARCHAR") private String description;
    @ManyToOne(optional = false) private Project project;
    @ManyToOne private ProjectVersion version;
    @ManyToOne private ProjectVersion occursIn;
    @Column(nullable = false) @Enumerated private Priority priority;
    @ManyToOne private Reporter assigned;
    @ManyToOne private Reporter author;

    // FIX Added reported timestamp
    @Temporal(TemporalType.TIMESTAMP) private Date reported;
    @Temporal(TemporalType.TIMESTAMP) private Date timestamp;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectVersion getVersion() {
        return version;
    }

    public void setVersion(ProjectVersion version) {
        this.version = version;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Reporter getAssigned() {
        return assigned;
    }

    public void setAssigned(Reporter assigned) {
        this.assigned = assigned;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ProjectVersion getOccursIn() {
        return occursIn;
    }

    public void setOccursIn(ProjectVersion occursIn) {
        this.occursIn = occursIn;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the date the report was filed
     */
    public Date getReportedTimestamp() {
        return reported;
    }

    /**
     * @param reported
     *            the date, the report was filed
     */
    public void setReportedTimestamp(Date reported) {
        this.reported = reported;
    }

    @PrePersist
    void updateDates() {
        // FIX Update timestamp every time
        timestamp = new Date();
    }

    public Reporter getAuthor() {
        return author;
    }

    public void setAuthor(Reporter author) {
        this.author = author;
    }
}
