package com.evry.fruktkorgservice.util;

import com.evry.fruktkorg.domain.model.Frukt;
import com.evry.fruktkorg.domain.model.Fruktkorg;
import com.evry.fruktkorg.domain.model.Report;
import com.evry.fruktkorgservice.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ModelUtilTest {

    @Test
    void convertFrukt() {
        Frukt frukt = new Frukt();
        frukt.setId(1);
        frukt.setAmount(1);
        frukt.setType("Banan");

        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Korg");
        fruktkorg.setId(1);
        fruktkorg.setFruktList(Collections.singletonList(frukt));

        frukt.setFruktkorg(fruktkorg);

        ImmutableFrukt immutableFrukt = ModelUtil.convertFrukt(frukt);
        assertEquals(frukt.getId(), immutableFrukt.getId(), "Id should be the same");
        assertEquals(frukt.getType(), immutableFrukt.getType(), "Type should be the same");
        assertEquals(frukt.getAmount(), immutableFrukt.getAmount(), "Type should be the same");
        assertEquals(frukt.getFruktkorg().getId(), immutableFrukt.getFruktkorgId(), "Id of fruktkorg should be the same");
    }

    @Test
    void convertImmutableFrukt() {
        ImmutableFrukt immutableFrukt = new ImmutableFruktBuilder()
                .setType("Banan")
                .setAmount(5)
                .setFruktkorgId(1)
                .createImmutableFrukt();
        Frukt frukt = ModelUtil.convertImmutableFrukt(immutableFrukt);
        assertEquals(immutableFrukt.getId(), frukt.getId(), "Id should be the same");
        assertEquals(immutableFrukt.getType(), frukt.getType(), "Type should be the same");
        assertEquals(immutableFrukt.getAmount(), frukt.getAmount(), "Amount should be the same");
        assertNull(frukt.getFruktkorg(), "Fruktkorg should not be set");

    }

    @Test
    void convertFruktkorg() {
        Frukt frukt = new Frukt();
        frukt.setId(1);
        frukt.setAmount(1);
        frukt.setType("Banan");

        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Korg");
        fruktkorg.setId(1);
        fruktkorg.setFruktList(Collections.singletonList(frukt));
        Instant updated = Instant.now();
        fruktkorg.setLastChanged(updated);

        frukt.setFruktkorg(fruktkorg);

        ImmutableFruktkorg immutableFruktkorg = ModelUtil.convertFruktkorg(fruktkorg);
        assertEquals(immutableFruktkorg.getId(), fruktkorg.getId(), "Id should be the same");
        assertEquals(immutableFruktkorg.getName(), fruktkorg.getName(), "Name should be the same");
        assertEquals(1, immutableFruktkorg.getFruktList().size(), "Amount of Frukt should be the same");
        assertEquals(updated, immutableFruktkorg.getLastChanged(), "Last Change should be the same");

        ImmutableFrukt convertedfrukt = immutableFruktkorg.getFruktList().get(0);
        assertEquals(frukt.getId(), convertedfrukt.getId(), "Id should be the same");
        assertEquals(frukt.getType(), convertedfrukt.getType(), "Type should be the same");
        assertEquals(frukt.getAmount(), convertedfrukt.getAmount(), "Amount of Frukt should be the same");
        assertEquals(frukt.getFruktkorg().getId(), convertedfrukt.getFruktkorgId(), "Id of Fruktkorg should be the same");

    }

    @Test
    void convertImmutableFruktkorg() {
        Instant updated = Instant.now();
        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .setId(1)
                .setLastChanged(updated)
                .addFrukt(new ImmutableFruktBuilder().
                        setType("Banan")
                        .setAmount(5)
                        .setId(1)
                        .setFruktkorgId(1)
                        .createImmutableFrukt()
                )
                .createImmutableFruktkorg();
        Fruktkorg fruktkorg = ModelUtil.convertImmutableFruktkorg(immutableFruktkorg);

        assertEquals(immutableFruktkorg.getId(), fruktkorg.getId(), "Id should be the same");
        assertEquals(immutableFruktkorg.getName(), fruktkorg.getName(), "Name should be the same");
        assertEquals(1, fruktkorg.getFruktList().size(), "Amount of Frukt should be the same");
        assertEquals(updated, fruktkorg.getLastChanged(), "Last Change should be the same");
        Frukt frukt = fruktkorg.getFruktList().get(0);
        ImmutableFrukt immutableFrukt = immutableFruktkorg.getFruktList().get(0);

        assertEquals(immutableFrukt.getId(), frukt.getId(), "Id should be the same");
        assertEquals(immutableFrukt.getType(), frukt.getType(), "Type should be the same");
        assertEquals(immutableFrukt.getAmount(), frukt.getAmount(), "Amount should be the same");
        assertEquals(immutableFrukt.getFruktkorgId(), frukt.getFruktkorg().getId(), "Id should be the same");
    }

    @Test
    void convertReport() {
        Instant created = Instant.now();

        Report report = new Report();
        report.setId(1);
        report.setLocation("fake/location/report.xmlconversion");
        report.setCreated(created);
        report.setRead(false);

        ImmutableReport immutableReport = ModelUtil.convertReport(report);
        assertEquals(immutableReport.getId(), report.getId(), "Id should be the same");
        assertEquals(immutableReport.getLocation(), report.getLocation(), "Location should be the same");
        assertEquals(immutableReport.isRead(), report.isRead(), "Read should be the same");
        assertEquals(created, immutableReport.getCreated(), "Created should be the same");
    }

    @Test
    void convertImmutableReport() {
        Instant created = Instant.now();
        ImmutableReport immutableReport = new ImmutableReportBuilder()
                .setId(1)
                .setLocation("fake/location/report.xmlconversion")
                .setCreated(created)
                .setRead(false)
                .createImmutableReport();

        Report report = ModelUtil.convertImmutableReport(immutableReport);

        assertEquals(immutableReport.getId(), report.getId(), "Id should be the same");
        assertEquals(immutableReport.getLocation(), report.getLocation(), "Location should be the same");
        assertEquals(immutableReport.getCreated(), report.getCreated(), "Created should be the same");
        assertEquals(immutableReport.isRead(), report.isRead(), "Read should be the same");
    }
}