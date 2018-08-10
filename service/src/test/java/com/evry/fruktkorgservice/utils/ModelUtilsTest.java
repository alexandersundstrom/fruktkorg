package com.evry.fruktkorgservice.utils;

import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktBuilder;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.model.ImmutableFruktkorgBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ModelUtilsTest {

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

        ImmutableFrukt immutableFrukt = ModelUtils.convertFrukt(frukt);
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
        Frukt frukt = ModelUtils.convertImmutableFrukt(immutableFrukt);
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

        frukt.setFruktkorg(fruktkorg);

        ImmutableFruktkorg immutableFruktkorg = ModelUtils.convertFruktkorg(fruktkorg);
        assertEquals(immutableFruktkorg.getId(), fruktkorg.getId(), "Id should be the same");
        assertEquals(immutableFruktkorg.getName(), fruktkorg.getName(), "Name should be the same");
        assertEquals(1, immutableFruktkorg.getFruktList().size(),  "Amount of Frukt should be the same");

        ImmutableFrukt convertedfrukt = immutableFruktkorg.getFruktList().get(0);
        assertEquals(frukt.getId(),convertedfrukt.getId(), "Id should be the same");
        assertEquals(frukt.getType(), convertedfrukt.getType(), "Type should be the same");
        assertEquals(frukt.getAmount(),convertedfrukt.getAmount(), "Amount of Frukt should be the same");
        assertEquals(frukt.getFruktkorg().getId(),convertedfrukt.getFruktkorgId(), "Id of Fruktkorg should be the same");

    }

    @Test
    void convertImmutableFruktkorg() {
        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .setId(1)
                .addFrukt(new ImmutableFruktBuilder().
                        setType("Banan")
                        .setAmount(5)
                        .setId(1)
                        .setFruktkorgId(1)
                        .createImmutableFrukt()
                )
                .createImmutableFruktkorg();
        Fruktkorg fruktkorg = ModelUtils.convertImmutableFruktkorg(immutableFruktkorg);

        assertEquals(immutableFruktkorg.getId(), fruktkorg.getId(), "Id should be the same");
        assertEquals(immutableFruktkorg.getName(), fruktkorg.getName(), "Name should be the same");
        assertEquals(1, fruktkorg.getFruktList().size(),  "Amount of Frukt should be the same");

        Frukt frukt = fruktkorg.getFruktList().get(0);
        ImmutableFrukt immutableFrukt = immutableFruktkorg.getFruktList().get(0);

        assertEquals(immutableFrukt.getId(), frukt.getId(), "Id should be the same");
        assertEquals(immutableFrukt.getType(), frukt.getType(), "Type should be the same");
        assertEquals(immutableFrukt.getAmount(), frukt.getAmount(), "Amount should be the same");
        assertEquals(immutableFrukt.getFruktkorgId(), frukt.getFruktkorg().getId(), "Id should be the same");
    }
}