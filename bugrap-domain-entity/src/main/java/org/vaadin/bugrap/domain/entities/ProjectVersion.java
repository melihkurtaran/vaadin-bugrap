package org.vaadin.bugrap.domain.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class ProjectVersion extends AbstractEntity implements
        Comparable<ProjectVersion> {
    private static final long serialVersionUID = 1L;

    private String version;

    @Temporal(TemporalType.DATE) private Date releaseDate;
    private boolean closed;

    @ManyToOne private Project project;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public String toString() {
        return version;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public int compareTo(ProjectVersion o) {
        return version.compareTo(o.version);
    }
}
