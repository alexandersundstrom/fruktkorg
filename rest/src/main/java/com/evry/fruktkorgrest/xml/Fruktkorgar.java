package com.evry.fruktkorgrest.xml;

import com.evry.fruktkorgservice.model.ImmutableFruktkorg;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "fruktkorgar")
public class Fruktkorgar {
    @XmlElement(name = "fruktkorg")
    public List<ImmutableFruktkorg> fruktkorgList;
}
