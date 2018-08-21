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
import com.evry.fruktkorgservice.xml.FruktkorgUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Arrays;
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
            fruktkorg.setName("Korg");

            return null;
        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .createImmutableFruktkorg();

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertNull(persistedFruktkorg.getLastChanged(), "Last Changed should only be set when Frukts are added");
    }

    @Test
    void createFruktkorgWithFrukt() {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg)arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setLastChanged(Instant.now());
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
        Assertions.assertNotNull(persistedFruktkorg.getLastChanged(), "Last Change should be set when at least 1 Frukt is provided");
    }

    @Test
    void addFruktToFruktkorg() throws FruktkorgMissingException {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg)arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Korg");
            fruktkorg.setLastChanged(Instant.now());

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
        Assertions.assertNotNull(persistedFruktkorg.getLastChanged(), "Last Change should be set when at least 1 Frukt is provided");
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

    @Test
    void listFruktkorgar() {
        Fruktkorg fruktkorg1 = new Fruktkorg();
        fruktkorg1.setId(1);
        fruktkorg1.setName("Korg 1");

        Fruktkorg fruktkorg2 = new Fruktkorg();
        fruktkorg2.setId(2);
        fruktkorg2.setName("Korg 2");

        Fruktkorg fruktkorg3 = new Fruktkorg();
        fruktkorg3.setId(3);
        fruktkorg3.setName("Korg 3");

        Mockito.when(fruktkorgDAO.listFruktkorgar())
                .thenReturn(Arrays.asList(fruktkorg1, fruktkorg2, fruktkorg3));

        List<ImmutableFruktkorg> fruktkorgList = fruktkorgService.listFruktkorgar();

        Assertions.assertEquals(3, fruktkorgList.size());
        Assertions.assertEquals("Korg 1", fruktkorgList.get(0).getName());
        Assertions.assertEquals("Korg 2", fruktkorgList.get(1).getName());
        Assertions.assertEquals("Korg 3", fruktkorgList.get(2).getName());
    }

    @Test
    void updateFruktkorg() throws FruktkorgMissingException {
        Fruktkorg fruktkorg1 = new Fruktkorg();
        fruktkorg1.setId(1);
        fruktkorg1.setName("Korg 1");
        fruktkorg1.setLastChanged(Instant.now());

        Frukt frukt1 = new Frukt("Banan", 5, fruktkorg1);
        frukt1.setId(1);

        fruktkorg1.getFruktList().add(frukt1);

        Mockito.when(fruktkorgDAO.findFruktkorgById(1)).thenReturn(Optional.of(fruktkorg1));
        Mockito.when(fruktkorgDAO.merge(fruktkorg1)).thenReturn(fruktkorg1);

        ImmutableFrukt updateFrukt1 = new ImmutableFruktBuilder()
                .setAmount(3)
                .setType("Kiwi")
                .createImmutableFrukt();

        ImmutableFrukt updateFrukt2 = new ImmutableFruktBuilder()
                .setAmount(6)
                .setType("Apelsin")
                .createImmutableFrukt();

        FruktkorgUpdate fruktkorgUpdate1 = new FruktkorgUpdate();
        fruktkorgUpdate1.id = 1;
        fruktkorgUpdate1.fruktList = Arrays.asList(updateFrukt1, updateFrukt2);

        ImmutableFruktkorg updatedFruktkorg = fruktkorgService.updateFruktkorg(fruktkorgUpdate1);

        Assertions.assertEquals(fruktkorg1.getId(), updatedFruktkorg.getId());
        Assertions.assertEquals(fruktkorg1.getName(), updatedFruktkorg.getName());
        Assertions.assertEquals(2, updatedFruktkorg.getFruktList().size());
    }
}
