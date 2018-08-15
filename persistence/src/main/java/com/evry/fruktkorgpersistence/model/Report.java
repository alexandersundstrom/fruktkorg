package com.evry.fruktkorgpersistence.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "report")
public class Report {
    @Id
    @SequenceGenerator(name = "reports_id_seq", sequenceName = "reports_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reports_id_seq")
    @Column(name = "report_id", updatable = false)
    private long id;

    @Column(name = "location")
    private String location;

    @Column(name = "created", columnDefinition = "TIMESTAMP DEFAULT now()")
    private Instant created;

    @Column(name = "read", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean read;

    public Report() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
