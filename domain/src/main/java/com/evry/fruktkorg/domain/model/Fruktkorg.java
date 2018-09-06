package com.evry.fruktkorg.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Fruktkorg {

    private long id;

    private String name;

    private List<Frukt> fruktList = new ArrayList<>();

    private Instant lastChanged;

    public Fruktkorg() {
    }

    public Fruktkorg(String name) {
        this.name = name;
    }

    public List<Frukt> getFruktList() {
        return fruktList;
    }

    public void setFruktList(List<Frukt> fruktList) {
        this.fruktList = fruktList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Instant lastChanged) {
        this.lastChanged = lastChanged;
    }

    @Override
    public String toString() {
        return "Fruktkorg{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fruktList=" + fruktList +
                ", lastChanged=" + lastChanged +
                '}';
    }
}
