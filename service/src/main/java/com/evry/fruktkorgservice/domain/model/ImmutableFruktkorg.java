package com.evry.fruktkorgservice.domain.model;

import com.evry.fruktkorgservice.xml.InstantAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;
import java.util.List;

@XmlRootElement(name = "fruktkorg")
public class ImmutableFruktkorg {
    @XmlElement(name = "id")
    private long id;
    @XmlElement(name = "name")
    private String name;
    @XmlElement(name = "frukt")
    private List<ImmutableFrukt> fruktList;
    @XmlElement(name = "lastChanged")
    @XmlJavaTypeAdapter(InstantAdapter.class)
    private Instant lastChanged;

    public ImmutableFruktkorg() {}

    ImmutableFruktkorg(long id, String name, List<ImmutableFrukt> fruktList, Instant lastChanged) {
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

    public Instant getLastChanged() {
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
