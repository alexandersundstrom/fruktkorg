package com.evry.fruktkorgservice.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "fruktkorgar")
public class FruktkorgarRestore {
    @XmlElement(name = "fruktkorg")
    public List<FruktkorgRestore> fruktkorgList;
}
