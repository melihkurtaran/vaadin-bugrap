package org.vaadin.bugrap.domain.entities;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractEntity implements BugrapEntity, Serializable {

    @Id @GeneratedValue(strategy = GenerationType.AUTO) private long id;

    @Version private int consistencyVersion = -1;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getConsistencyVersion() {
        return consistencyVersion;
    }

    public void setConsistencyVersion(int consistencyVersion) {
        this.consistencyVersion = consistencyVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEntity) {
            BugrapEntity r = (BugrapEntity) obj;
            return getId() == r.getId();
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }
}
