package com.evry.fruktkorgrest.model;

import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FruktkorgDTO {
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM HH:mm").withLocale(Locale.forLanguageTag("sv-SE")).withZone(ZoneId.systemDefault());
    private long id;
    private String name;
    private List<ImmutableFrukt> fruktList;
    private String lastChanged;

    public FruktkorgDTO() {

    }

    public FruktkorgDTO(ImmutableFruktkorg immutableFruktkorg) {
        this.lastChanged = immutableFruktkorg.getLastChanged() != null ? dateTimeFormatter.format(immutableFruktkorg.getLastChanged()): null;
        this.id = immutableFruktkorg.getId();
        this.name = immutableFruktkorg.getName();
        this.fruktList = immutableFruktkorg.getFruktList();
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

    public List<ImmutableFrukt> getFruktList() {
        return fruktList;
    }

    public void setFruktList(List<ImmutableFrukt> fruktList) {
        this.fruktList = fruktList;
    }

    public String getLastChanged() {
        return lastChanged;
    }

    public void setLastChangedFromInstant(Instant lastChanged) {
        this.lastChanged = lastChanged != null ? dateTimeFormatter.format(lastChanged): null;
    }

    public void setLastChanged(String lastChanged) {
        this.lastChanged = lastChanged;
    }
}
