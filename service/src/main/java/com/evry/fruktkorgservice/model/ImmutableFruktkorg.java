package com.evry.fruktkorgservice.model;

import java.util.List;

public class ImmutableFruktkorg {
    private long id;
    private String name;
    private List<ImmutableFrukt> fruktList;

    ImmutableFruktkorg(long id, String name, List<ImmutableFrukt> fruktList) {
        this.id = id;
        this.name = name;
        this.fruktList = fruktList;
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
}