package com.evry.fruktkorgservice.utils;

import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktBuilder;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.model.ImmutableFruktkorgBuilder;

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
}
