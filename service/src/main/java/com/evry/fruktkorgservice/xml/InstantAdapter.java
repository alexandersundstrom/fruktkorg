package com.evry.fruktkorgservice.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.Instant;

public class InstantAdapter extends XmlAdapter<AdaptedInstant, Instant> {

    @Override
    public Instant unmarshal(AdaptedInstant adaptedInstant) throws Exception {
        return Instant.ofEpochMilli(adaptedInstant.getEpochMilli());
    }

    @Override
    public AdaptedInstant marshal(Instant instant) throws Exception {
        AdaptedInstant adaptedInstant = new AdaptedInstant();
        adaptedInstant.setEpochMilli(instant.toEpochMilli());
        return adaptedInstant;
    }
}
