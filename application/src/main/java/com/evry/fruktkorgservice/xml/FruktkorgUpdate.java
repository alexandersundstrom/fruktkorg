package com.evry.fruktkorgservice.xml;

import com.evry.fruktkorgservice.domain.model.ImmutableFrukt;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "fruktkorg")
public class FruktkorgUpdate {
    @XmlElement(name = "id")
    public long id;
    @XmlElement(name = "frukt")
    public List<ImmutableFrukt> fruktList;
}
