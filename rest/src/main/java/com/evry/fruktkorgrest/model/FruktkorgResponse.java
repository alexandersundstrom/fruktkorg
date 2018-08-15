package com.evry.fruktkorgrest.model;

import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FruktkorgResponse {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMMM HH:mm", Locale.forLanguageTag("sv-SE"));
    private long id;
    private String name;
    private List<ImmutableFrukt> fruktList;
    private String lastChanged;

    public FruktkorgResponse() {

    }

    public FruktkorgResponse(ImmutableFruktkorg immutableFruktkorg) {
        this.lastChanged = immutableFruktkorg.getLastChanged() != null ? simpleDateFormat.format(immutableFruktkorg.getLastChanged()): null;
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

    public void setLastChangedFromTimeStamp(Timestamp lastChanged) {
        this.lastChanged = lastChanged != null ? simpleDateFormat.format(lastChanged): null;
    }

    public void setLastChanged(String lastChanged) {
        this.lastChanged = lastChanged;
    }
}
