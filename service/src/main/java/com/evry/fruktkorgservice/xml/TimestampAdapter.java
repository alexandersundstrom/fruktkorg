package com.evry.fruktkorgservice.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Timestamp;

public class TimestampAdapter extends XmlAdapter<AdaptedTimestamp, Timestamp> {

    @Override
    public Timestamp unmarshal(AdaptedTimestamp adaptedTimestamp) throws Exception {
        return new Timestamp(adaptedTimestamp.getNanos());
    }

    @Override
    public AdaptedTimestamp marshal(Timestamp timestamp) throws Exception {
        AdaptedTimestamp adaptedTimestamp = new AdaptedTimestamp();
        adaptedTimestamp.setNanos(timestamp.getNanos());
        return adaptedTimestamp;
    }
}
