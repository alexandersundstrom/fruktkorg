package com.evry.fruktkorgservice.domain.model;

import java.io.Serializable;
import java.time.Instant;

public class ImmutableReport implements Serializable {
    private long id;
    private String location;
    private Instant created;
    private boolean read;

    private ImmutableReport() {}

    ImmutableReport(long id, String location, Instant created, boolean read) {
        this.id = id;
        this.location = location;
        this.created = created;
        this.read = read;
    }

    public long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public Instant getCreated() {
        return created;
    }

    public boolean isRead() {
        return read;
    }
}
