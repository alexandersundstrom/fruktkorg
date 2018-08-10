package com.evry.fruktkorgservice;

import com.evry.fruktkorgpersistence.dao.FruktkorgDAO;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktBuilder;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.model.ImmutableFruktkorgBuilder;
import com.evry.fruktkorgservice.service.FruktkorgService;
import com.evry.fruktkorgservice.service.FruktkorgServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

class FruktkorgTest {

    private FruktkorgDAO fruktkorgDAO;
    private FruktkorgService fruktkorgService;

    @BeforeEach
    void init() {
        fruktkorgDAO = Mockito.mock(FruktkorgDAO.class);
        fruktkorgService = new FruktkorgServiceImpl(fruktkorgDAO);
    }

    @Test
    void createFruktkorg() {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg)arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Korg");

            return null;
        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .createImmutableFruktkorg();

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
    }

    @Test
    void createFruktkorgWithFrukt() {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg)arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Korg");

            Frukt frukt = new Frukt();
            frukt.setId(1);
            frukt.setType("Banan");
            frukt.setAmount(5);
            frukt.setFruktkorg(fruktkorg);

            fruktkorg.getFruktList().clear();
            fruktkorg.getFruktList().add(frukt);
            return null;

        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        ImmutableFrukt immutableFrukt = new ImmutableFruktBuilder().setType("Banan")
                .setAmount(5)
                .createImmutableFrukt();

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .addFrukt(immutableFrukt)
                .createImmutableFruktkorg();

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertEquals(1, persistedFruktkorg.getFruktList().size(), "Should be one Frukt in Fruktkorg" );
    }

    @Test
    void addFruktToFruktkorg() throws FruktkorgMissingException {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg)arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Korg");

            return null;
        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);
        Mockito.when(fruktkorgDAO.findFruktkorgById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .createImmutableFruktkorg();

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertEquals(0, persistedFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 0" );

        ImmutableFrukt fruktToAdd = new ImmutableFruktBuilder()
                .setType("banan")
                .setAmount(50)
                .setFruktkorgId(persistedFruktkorg.getId())
                .createImmutableFrukt();

        persistedFruktkorg = fruktkorgService.addFruktToFruktkorg(persistedFruktkorg.getId(), fruktToAdd);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertEquals(1, persistedFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1" );
    }

    @Test
    void addFruktToMissingFruktkorg() {
        Mockito.when(fruktkorgDAO.findFruktkorgById(1)).thenReturn(Optional.empty());

        ImmutableFrukt fruktToAdd = new ImmutableFruktBuilder()
                .setType("banan")
                .setAmount(50)
                .setFruktkorgId(1)
                .createImmutableFrukt();

        Assertions.assertThrows(FruktkorgMissingException.class, () -> {
            fruktkorgService.addFruktToFruktkorg(1, fruktToAdd);
        });
    }

    @Test
    void addFruktToFruktkorgWithFruktAlreadyExisting() throws FruktkorgMissingException {
        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);

        Frukt returnFrukt = new Frukt("banan", 5, returnFruktkorg);
        returnFrukt.setId(1);
        returnFruktkorg.getFruktList().add(returnFrukt);
        Mockito.when(fruktkorgDAO.findFruktkorgById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.addFruktToFruktkorg(1, new ImmutableFruktBuilder()
            .setType("banan")
            .setAmount(2)
            .setFruktkorgId(1)
            .createImmutableFrukt());

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertEquals(1, persistedFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1" );
        Assertions.assertEquals(7, persistedFruktkorg.getFruktList().get(0).getAmount(), "Bananas in fruktkorg should be 7" );
    }
}
