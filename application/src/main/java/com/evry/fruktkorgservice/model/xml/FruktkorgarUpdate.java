package com.evry.fruktkorgservice.model.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "fruktkorgar")
public class FruktkorgarUpdate {
    @XmlElement(name = "fruktkorg")
    public List<FruktkorgUpdate> fruktkorgList;
}
