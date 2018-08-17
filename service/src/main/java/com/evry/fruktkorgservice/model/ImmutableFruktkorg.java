package com.evry.fruktkorgservice.model;

import com.evry.fruktkorgservice.xml.TimestampAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.sql.Timestamp;
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
    @XmlJavaTypeAdapter(TimestampAdapter.class)
    private Timestamp lastChanged;

    public ImmutableFruktkorg() {}

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
