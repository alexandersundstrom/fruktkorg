package com.evry.fruktkorgservice.xml;

import com.evry.fruktkorgservice.model.ImmutableFrukt;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class FruktkorgUpdate {
    @XmlElement(name = "id")
    public long id;
    @XmlElement(name = "frukt")
    public List<ImmutableFrukt> fruktList;
}