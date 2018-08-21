package com.evry.fruktkorgservice.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class FruktkorgarUpdate {
    @XmlElement(name = "fruktkorg")
    public List<FruktkorgUpdate> fruktkorgList;
}
