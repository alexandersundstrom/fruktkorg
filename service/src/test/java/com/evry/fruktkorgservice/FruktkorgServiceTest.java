package com.evry.fruktkorgservice;

import com.evry.fruktkorgpersistence.dao.FruktkorgDAO;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgservice.exception.FruktMissingException;
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

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class FruktkorgServiceTest {

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
            fruktkorg.setLastChanged(new Timestamp(System.currentTimeMillis()));
            fruktkorg.setName("Korg");

            return null;
        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .createImmutableFruktkorg();

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertNotNull(persistedFruktkorg.getLastChanged(), "Last Change should be set by default");
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
            fruktkorg.setLastChanged(new Timestamp(System.currentTimeMillis()));

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
        Assertions.assertNotNull(persistedFruktkorg.getLastChanged(), "Last Change should be set by default");

        Timestamp lastChanged = persistedFruktkorg.getLastChanged();

        ImmutableFrukt fruktToAdd = new ImmutableFruktBuilder()
                .setType("banan")
                .setAmount(50)
                .setFruktkorgId(persistedFruktkorg.getId())
                .createImmutableFrukt();

        persistedFruktkorg = fruktkorgService.addFruktToFruktkorg(persistedFruktkorg.getId(), fruktToAdd);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertEquals(1, persistedFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1" );
        Assertions.assertNotEquals(lastChanged, persistedFruktkorg.getLastChanged(),    "Last Changed should be updated when Fruktkorg changes");
    }

    @Test
    void addFruktToMissingFruktkorg() {
        Mockito.when(fruktkorgDAO.findFruktkorgById(1)).thenReturn(Optional.empty());

        ImmutableFrukt fruktToAdd = new ImmutableFruktBuilder()
                .setType("banan")
                .setAmount(50)
                .setFruktkorgId(1)
                .createImmutableFrukt();

        Assertions.assertThrows(FruktkorgMissingException.class, () -> fruktkorgService.addFruktToFruktkorg(1, fruktToAdd));
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

    @Test
    void removeSomeFruktFromFruktkorg() throws FruktkorgMissingException, FruktMissingException {
        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);

        Frukt returnFrukt = new Frukt("banan", 5, returnFruktkorg);
        returnFrukt.setId(1);
        returnFruktkorg.getFruktList().add(returnFrukt);
        Mockito.when(fruktkorgDAO.findFruktkorgById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        ImmutableFruktkorg immutableFruktkorg = fruktkorgService.removeFruktFromFruktkorg(1, "banan", 3);

        Assertions.assertEquals(1, immutableFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", immutableFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertEquals(1, immutableFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1" );
        Assertions.assertEquals(2, immutableFruktkorg.getFruktList().get(0).getAmount(), "Bananas in fruktkorg should be 2" );
    }

    @Test
    void removeAllFruktFromFruktkorg() throws FruktkorgMissingException, FruktMissingException {
        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);

        Frukt returnFrukt = new Frukt("banan", 5, returnFruktkorg);
        returnFrukt.setId(1);
        returnFruktkorg.getFruktList().add(returnFrukt);
        Mockito.when(fruktkorgDAO.findFruktkorgById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        ImmutableFruktkorg immutableFruktkorg = fruktkorgService.removeFruktFromFruktkorg(1, "banan", 5);

        Assertions.assertEquals(1, immutableFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", immutableFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertEquals(0, immutableFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1" );
    }

    @Test
    void removeMissingFruktFromFruktkorg() {
        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);

        Mockito.when(fruktkorgDAO.findFruktkorgById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        Assertions.assertThrows(FruktMissingException.class, () -> fruktkorgService.removeFruktFromFruktkorg(1, "banan", 5));
    }

    @Test
    void removeFruktFromMissingFruktkorg() {
        Mockito.when(fruktkorgDAO.findFruktkorgById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(FruktkorgMissingException.class, () -> fruktkorgService.removeFruktFromFruktkorg(1, "banan", 5));
    }

    @Test
    void searchFruktkorgByFrukt() {
        Frukt frukt = new Frukt();
        frukt.setId(1);
        frukt.setAmount(5);
        frukt.setType("Super Banan");

        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setId(1);
        fruktkorg.setName("Korg");
        frukt.setFruktkorg(fruktkorg);
        fruktkorg.getFruktList().add(frukt);

        Mockito.when(fruktkorgDAO.findFruktkorgByFrukt("Super Banan"))
                .thenReturn(Collections.singletonList(fruktkorg));

        List<ImmutableFruktkorg> fruktkorgList = fruktkorgService.searchFruktkorgByFrukt("Super Banan");

        Assertions.assertEquals(1, fruktkorgList.size());
        ImmutableFruktkorg immutableFruktkorg = fruktkorgList.get(0);
        Assertions.assertEquals(1, immutableFruktkorg.getId());
        Assertions.assertEquals("Korg", immutableFruktkorg.getName());
        Assertions.assertEquals(1, immutableFruktkorg.getFruktList().size());
        Assertions.assertEquals("Super Banan", immutableFruktkorg.getFruktList().get(0).getType());
    }

    @Test
    void searchFruktkorgByMissingFrukt() {
        Frukt frukt = new Frukt();
        frukt.setId(1);
        frukt.setAmount(5);
        frukt.setType("Super Banan");

        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setId(1);
        fruktkorg.setName("Korg");
        frukt.setFruktkorg(fruktkorg);
        fruktkorg.getFruktList().add(frukt);

        Mockito.when(fruktkorgDAO.findFruktkorgByFrukt("Super Banan"))
                .thenReturn(Collections.singletonList(fruktkorg));

        List<ImmutableFruktkorg> fruktkorgList = fruktkorgService.searchFruktkorgByFrukt("Vanlig Banan");

        Assertions.assertEquals(0, fruktkorgList.size());
    }
}
