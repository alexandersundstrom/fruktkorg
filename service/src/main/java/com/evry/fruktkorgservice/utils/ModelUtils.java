package com.evry.fruktkorgservice.utils;

import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgpersistence.model.Report;
import com.evry.fruktkorgservice.domain.model.*;
import com.evry.fruktkorgservice.utils.builders.ImmutableFruktBuilder;
import com.evry.fruktkorgservice.utils.builders.ImmutableFruktkorgBuilder;
import com.evry.fruktkorgservice.utils.builders.ImmutableReportBuilder;

public class ModelUtils {
    public static ImmutableFrukt convertFrukt(Frukt frukt) {
        return new ImmutableFruktBuilder()
                .setId(frukt.getId())
                .setType(frukt.getType())
                .setAmount(frukt.getAmount())
                .setFruktkorgId(frukt.getFruktkorg().getId())
                .createImmutableFrukt();
    }

    public static Frukt convertImmutableFrukt(ImmutableFrukt immutableFrukt) {
        Frukt frukt = new Frukt();
        frukt.setId(immutableFrukt.getId());
        frukt.setType(immutableFrukt.getType());
        frukt.setAmount(immutableFrukt.getAmount());
        return frukt;
    }

    public static ImmutableFruktkorg convertFruktkorg(Fruktkorg fruktkorg) {
        ImmutableFruktkorgBuilder immutableFruktkorgBuilder = new ImmutableFruktkorgBuilder()
                .setId(fruktkorg.getId())
                .setName(fruktkorg.getName())
                .setLastChanged(fruktkorg.getLastChanged());

        for(Frukt frukt : fruktkorg.getFruktList()) {
            immutableFruktkorgBuilder.addFrukt(convertFrukt(frukt));
        }

        return immutableFruktkorgBuilder.createImmutableFruktkorg();
    }

    public static Fruktkorg convertImmutableFruktkorg(ImmutableFruktkorg immutableFruktkorg) {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setId(immutableFruktkorg.getId());
        fruktkorg.setName(immutableFruktkorg.getName());
        fruktkorg.setLastChanged(immutableFruktkorg.getLastChanged());

        for(ImmutableFrukt immutableFrukt : immutableFruktkorg.getFruktList()) {
            Frukt frukt = convertImmutableFrukt(immutableFrukt);
            frukt.setFruktkorg(fruktkorg);
            fruktkorg.getFruktList().add(frukt);
        }

        return fruktkorg;
    }

    public static ImmutableReport convertReport(Report report) {
        return new ImmutableReportBuilder()
                .setId(report.getId())
                .setLocation(report.getLocation())
                .setCreated(report.getCreated())
                .setRead(report.isRead())
                .createImmutableReport();
    }

    public static Report convertImmutableReport(ImmutableReport immutableReport) {
        Report report = new Report();
        report.setId(immutableReport.getId());
        report.setLocation(immutableReport.getLocation());
        report.setCreated(immutableReport.getCreated());
        report.setRead(immutableReport.isRead());

        return report;
    }
}
