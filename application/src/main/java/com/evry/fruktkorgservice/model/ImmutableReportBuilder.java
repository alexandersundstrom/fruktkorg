package com.evry.fruktkorgservice.model;

import java.time.Instant;

public class ImmutableReportBuilder {
    private long id;
    private String location;
    private Instant created;
    private boolean read;

    public ImmutableReportBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public ImmutableReportBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public ImmutableReportBuilder setCreated(Instant created) {
        this.created = created;
        return this;
    }

    public ImmutableReportBuilder setRead(boolean read) {
        this.read = read;
        return this;
    }

    public ImmutableReport createImmutableReport() {
        return new ImmutableReport(id, location, created, read);
    }
}