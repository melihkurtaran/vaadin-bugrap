package org.vaadin.bugrap.domain.entities;

import java.util.List;

import javax.persistence.*;

@Entity
public class Project extends AbstractEntity implements Comparable<Project> {
    private static final long serialVersionUID = 1L;

    private String name;

    @ManyToOne private Reporter manager;

    @ManyToMany(fetch = FetchType.LAZY) private List<Reporter> developers;

    @Override
    public int compareTo(Project o) {
        return name.compareToIgnoreCase(o.name);
    }

    public List<Reporter> getDevelopers() {
        return developers;
    }

    public Reporter getManager() {
        return manager;
    }

    public String getName() {
        return name;
    }

    public void setDevelopers(List<Reporter> developers) {
        this.developers = developers;
    }

    public void setManager(Reporter manager) {
        this.manager = manager;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
