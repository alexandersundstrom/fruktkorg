package com.evry.fruktkorgservice.model;

import java.sql.Timestamp;
import java.util.List;

public class ImmutableFruktkorg {
    private long id;
    private String name;
    private List<ImmutableFrukt> fruktList;
    private Timestamp lastChanged;

    private ImmutableFruktkorg() {}

    ImmutableFruktkorg(long id, String name, List<ImmutableFrukt> fruktList, Timestamp lastChanged) {
        this.id = id;
        this.name = name;
        this.fruktList = fruktList;
        this.lastChanged = lastChanged;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ImmutableFrukt> getFruktList() {
        return fruktList;
    }

    public Timestamp getLastChanged() {
        return lastChanged;
    }

    @Override
    public String toString() {
        return "ImmutableFruktkorg{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fruktList=" + fruktList +
                ", lastChanged=" + lastChanged +
                '}';
    }
}
