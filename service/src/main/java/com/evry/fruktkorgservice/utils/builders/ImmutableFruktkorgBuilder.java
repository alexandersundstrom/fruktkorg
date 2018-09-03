package com.evry.fruktkorgservice.utils.builders;

import com.evry.fruktkorgservice.domain.model.ImmutableFrukt;
import com.evry.fruktkorgservice.domain.model.ImmutableFruktkorg;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImmutableFruktkorgBuilder {
    private long id;
    private String name;
    private List<ImmutableFrukt> fruktList = new ArrayList<>();
    private Instant lastChanged;

    public ImmutableFruktkorgBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public ImmutableFruktkorgBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ImmutableFruktkorgBuilder setLastChanged(Instant lastChanged) {
        this.lastChanged = lastChanged;
        return this;
    }

    public ImmutableFruktkorgBuilder setFruktList(List<ImmutableFrukt> fruktList) {
        this.fruktList = fruktList;
        return this;
    }

    public ImmutableFruktkorgBuilder addFrukt(ImmutableFrukt frukt) {
        this.fruktList.add(frukt);
        return this;
    }

    public ImmutableFruktkorg createImmutableFruktkorg() {
        return new ImmutableFruktkorg(id, name, Collections.unmodifiableList(fruktList), lastChanged);
    }
}