package com.evry.fruktkorgservice;

import com.evry.fruktkorgpersistence.dao.FruktDAO;
import com.evry.fruktkorgpersistence.dao.FruktkorgDAO;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgservice.domain.model.ImmutableFrukt;
import com.evry.fruktkorgservice.domain.model.ImmutableFruktBuilder;
import com.evry.fruktkorgservice.domain.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.domain.model.ImmutableFruktkorgBuilder;
import com.evry.fruktkorgservice.domain.service.FruktkorgService;
import com.evry.fruktkorgservice.exception.FruktMissingException;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.xml.FruktkorgUpdate;
import com.evry.fruktkorgservice.xml.FruktkorgarUpdate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class FruktkorgServiceTest {

    private FruktkorgDAO fruktkorgDAO;
    private FruktDAO fruktDAO;
    private FruktkorgService fruktkorgService;

    private static final String updateFruktkorgarXML = "" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<fruktkorgar>" +
            "   <fruktkorg>" +
            "       <id>1</id>" +
            "       <frukt>" +
            "           <type>Kiwi</type>" +
            "           <amount>3</amount>" +
            "       </frukt>" +
            "       <frukt>" +
            "           <type>Apelsin</type>" +
            "           <amount>6</amount>" +
            "       </frukt>" +
            "   </fruktkorg>" +
            "</fruktkorgar>";

    private static final String restoreExistingFruktkorgXML = "" +
            "<?xml version=\"1.0\"?>" +
            "<fruktkorgar>" +
            "   <fruktkorg>" +
            "       <id>1</id>" +
            "       <name>Köket</name>" +
            "       <frukt>" +
            "           <id>1</id>" +
            "           <type>Kiwi</type>" +
            "           <amount>10</amount>" +
            "       </frukt>" +
            "       <frukt>" +
            "           <type>Apelsin</type>" +
            "           <amount>10</amount>" +
            "       </frukt>" +
            "   </fruktkorg>" +
            "</fruktkorgar>";

    private static final String restoreNewFruktkorgXML = "" +
            "<?xml version=\"1.0\"?>" +
            "<fruktkorgar>" +
            "   <fruktkorg>" +
            "       <name>Köket</name>" +
            "       <frukt>" +
            "           <type>Kiwi</type>" +
            "           <amount>10</amount>" +
            "       </frukt>" +
            "       <frukt>" +
            "           <type>Apelsin</type>" +
            "           <amount>10</amount>" +
            "       </frukt>" +
            "   </fruktkorg>" +
            "</fruktkorgar>";

    @BeforeEach
    void init() {
        fruktkorgDAO = Mockito.mock(FruktkorgDAO.class);
        fruktDAO = Mockito.mock(FruktDAO.class);
        fruktkorgService = new FruktkorgService(fruktkorgDAO, fruktDAO);
    }

    @Test
    void createFruktkorg() {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg) arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Korg");

            return null;
        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .createImmutableFruktkorg();

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one");
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg");
        Assertions.assertNull(persistedFruktkorg.getLastChanged(), "Last Changed should only be set when Frukts are added");
    }

    @Test
    void createFruktkorgWithFrukt() {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg) arguments[0];
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

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one");
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg");
        Assertions.assertEquals(1, persistedFruktkorg.getFruktList().size(), "Should be one Frukt in Fruktkorg");
        Assertions.assertNotNull(persistedFruktkorg.getLastChanged(), "Last Change should be set when at least 1 Frukt is provided");
    }

    @Test
    void addFruktToFruktkorg() throws FruktkorgMissingException {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg) arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Korg");
            fruktkorg.setLastChanged(Instant.now());

            return null;
        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);
        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .createImmutableFruktkorg();

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one");
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg");
        Assertions.assertEquals(0, persistedFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 0");

        ImmutableFrukt fruktToAdd = new ImmutableFruktBuilder()
                .setType("banan")
                .setAmount(50)
                .setFruktkorgId(persistedFruktkorg.getId())
                .createImmutableFrukt();

        persistedFruktkorg = fruktkorgService.addFruktToFruktkorg(persistedFruktkorg.getId(), fruktToAdd);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one");
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg");
        Assertions.assertEquals(1, persistedFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1");
        Assertions.assertNotNull(persistedFruktkorg.getLastChanged(), "Last Change should be set when at least 1 Frukt is provided");
    }

    @Test
    void addFruktToMissingFruktkorg() {
        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.empty());

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
        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.addFruktToFruktkorg(1, new ImmutableFruktBuilder()
                .setType("banan")
                .setAmount(2)
                .setFruktkorgId(1)
                .createImmutableFrukt());

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one");
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg");
        Assertions.assertEquals(1, persistedFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1");
        Assertions.assertEquals(7, persistedFruktkorg.getFruktList().get(0).getAmount(), "Bananas in fruktkorg should be 7");
    }

    @Test
    void removeSomeFruktFromFruktkorg() throws FruktkorgMissingException, FruktMissingException {
        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);

        Frukt returnFrukt = new Frukt("banan", 5, returnFruktkorg);
        returnFrukt.setId(1);
        returnFruktkorg.getFruktList().add(returnFrukt);
        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        ImmutableFruktkorg immutableFruktkorg = fruktkorgService.removeFruktFromFruktkorg(1, "banan", 3);

        Assertions.assertEquals(1, immutableFruktkorg.getId(), "Id should be set to one");
        Assertions.assertEquals("Korg", immutableFruktkorg.getName(), "Name should be Korg");
        Assertions.assertEquals(1, immutableFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1");
        Assertions.assertEquals(2, immutableFruktkorg.getFruktList().get(0).getAmount(), "Bananas in fruktkorg should be 2");
    }

    @Test
    void removeAllFruktFromFruktkorg() throws FruktkorgMissingException, FruktMissingException {
        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);

        Frukt returnFrukt = new Frukt("banan", 5, returnFruktkorg);
        returnFrukt.setId(1);
        returnFruktkorg.getFruktList().add(returnFrukt);
        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        ImmutableFruktkorg immutableFruktkorg = fruktkorgService.removeFruktFromFruktkorg(1, "banan", 5);

        Assertions.assertEquals(1, immutableFruktkorg.getId(), "Id should be set to one");
        Assertions.assertEquals("Korg", immutableFruktkorg.getName(), "Name should be Korg");
        Assertions.assertEquals(0, immutableFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1");
    }

    @Test
    void removeMissingFruktFromFruktkorg() {
        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);

        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgDAO.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        Assertions.assertThrows(FruktMissingException.class, () -> fruktkorgService.removeFruktFromFruktkorg(1, "banan", 5));
    }

    @Test
    void removeFruktFromMissingFruktkorg() {
        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.empty());

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

        Mockito.when(fruktkorgDAO.findAllByFruktType("Super Banan"))
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

        Mockito.when(fruktkorgDAO.findAllByFruktType("Super Banan"))
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

        Mockito.when(fruktkorgDAO.findAll())
                .thenReturn(Arrays.asList(fruktkorg1, fruktkorg2, fruktkorg3));

        List<ImmutableFruktkorg> fruktkorgList = fruktkorgService.listFruktkorgar();

        Assertions.assertEquals(3, fruktkorgList.size());
        Assertions.assertEquals("Korg 1", fruktkorgList.get(0).getName());
        Assertions.assertEquals("Korg 2", fruktkorgList.get(1).getName());
        Assertions.assertEquals("Korg 3", fruktkorgList.get(2).getName());
    }

    @Test
    void updateFruktkorgar() throws Exception {
        Fruktkorg fruktkorg1 = new Fruktkorg();
        fruktkorg1.setId(1);
        fruktkorg1.setName("Korg 1");
        fruktkorg1.setLastChanged(Instant.now());

        Frukt frukt1 = new Frukt("Banan", 5, fruktkorg1);
        frukt1.setId(1);

        fruktkorg1.getFruktList().add(frukt1);

        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.of(fruktkorg1));
        Mockito.when(fruktkorgDAO.merge(fruktkorg1)).thenReturn(fruktkorg1);

        ImmutableFrukt updateFrukt1 = new ImmutableFruktBuilder()
                .setAmount(3)
                .setType("Kiwi")
                .createImmutableFrukt();

        ImmutableFrukt updateFrukt2 = new ImmutableFruktBuilder()
                .setAmount(6)
                .setType("Apelsin")
                .createImmutableFrukt();

        FruktkorgarUpdate fruktkorgarUpdate = new FruktkorgarUpdate();

        FruktkorgUpdate fruktkorgUpdate1 = new FruktkorgUpdate();
        fruktkorgUpdate1.id = 1;
        fruktkorgUpdate1.fruktList = Arrays.asList(updateFrukt1, updateFrukt2);

        fruktkorgarUpdate.fruktkorgList = Collections.singletonList(fruktkorgUpdate1);

        List<ImmutableFruktkorg> updatedFruktkorgar = fruktkorgService.updateFruktkorgar(new ByteArrayInputStream(updateFruktkorgarXML.getBytes()));

        Assertions.assertEquals(fruktkorg1.getId(), updatedFruktkorgar.get(0).getId());
        Assertions.assertEquals(fruktkorg1.getName(), updatedFruktkorgar.get(0).getName());
        Assertions.assertEquals(2, updatedFruktkorgar.get(0).getFruktList().size());
    }

    @Test
    void restorePersistedFruktkorg() throws Exception {
        Fruktkorg persistedKitchenfruktkorg = new Fruktkorg();
        persistedKitchenfruktkorg.setId(1);
        persistedKitchenfruktkorg.setName("Köket");
        persistedKitchenfruktkorg.setLastChanged(Instant.now());

        Frukt persistedKiwi = new Frukt("Banan", 10, persistedKitchenfruktkorg);
        persistedKiwi.setId(1);

        persistedKitchenfruktkorg.getFruktList().add(persistedKiwi);

        Fruktkorg kitchenUpdatedFromXML = new Fruktkorg();
        kitchenUpdatedFromXML.setName("Köket");
        kitchenUpdatedFromXML.setId(1);

        Frukt kiwiUpdated = new Frukt("Kiwi", 10, kitchenUpdatedFromXML);
        kiwiUpdated.setId(1);

        Frukt apelsinUpdated = new Frukt("Apelsin", 10, kitchenUpdatedFromXML);
        apelsinUpdated.setId(2);

        kitchenUpdatedFromXML.getFruktList().add(kiwiUpdated);
        kitchenUpdatedFromXML.getFruktList().add(apelsinUpdated);

        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.of(persistedKitchenfruktkorg));
        Mockito.when(fruktDAO.findById(1)).thenReturn(Optional.of(persistedKiwi));
        Mockito.when(fruktkorgDAO.merge(Mockito.any())).thenReturn(kitchenUpdatedFromXML);

        List<ImmutableFruktkorg> restoredFruktkorgar = fruktkorgService.restoreFruktkorgar(new ByteArrayInputStream(restoreExistingFruktkorgXML.getBytes()));

        Assertions.assertEquals(1, restoredFruktkorgar.size(), "Should only be one Fruktkorg");
        Assertions.assertEquals(2, restoredFruktkorgar.get(0).getFruktList().size(), "Should only be two Frukter");
        Assertions.assertEquals("Köket", restoredFruktkorgar.get(0).getName());

    }

    @Test
    void restoreNewFruktkorg() throws Exception {
        Fruktkorg kitchenUpdatedFromXML = new Fruktkorg();
        kitchenUpdatedFromXML.setName("Köket");
        kitchenUpdatedFromXML.setId(1);

        Frukt kiwiUpdated = new Frukt("Kiwi", 10, kitchenUpdatedFromXML);
        kiwiUpdated.setId(1);

        Frukt apelsinUpdated = new Frukt("Apelsin", 10, kitchenUpdatedFromXML);
        apelsinUpdated.setId(2);

        kitchenUpdatedFromXML.getFruktList().add(kiwiUpdated);
        kitchenUpdatedFromXML.getFruktList().add(apelsinUpdated);

        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg) arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Köket");

            return null;
        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        Mockito.when(fruktkorgDAO.merge(Mockito.any())).thenReturn(kitchenUpdatedFromXML);

        List<ImmutableFruktkorg> restoredFruktkorgar = fruktkorgService.restoreFruktkorgar(new ByteArrayInputStream(restoreNewFruktkorgXML.getBytes()));

        Assertions.assertEquals(1, restoredFruktkorgar.size(), "Should only be one Fruktkorg");
        Assertions.assertEquals(2, restoredFruktkorgar.get(0).getFruktList().size(), "Should only be two Frukter");

    }

    @Test
    void restoreWithFruktkorgIdNotFound() {
        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(FruktkorgMissingException.class, () -> fruktkorgService.updateFruktkorgar(new ByteArrayInputStream(updateFruktkorgarXML.getBytes())));

    }

    @Test
    void updateWithFruktkorgIdNotFound() {
        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(FruktkorgMissingException.class, () -> fruktkorgService.restoreFruktkorgar(new ByteArrayInputStream(restoreExistingFruktkorgXML.getBytes())));

    }


    @Test
    void restoreWithFruktIdNotFound() {
        Fruktkorg persistedKitchenfruktkorg = new Fruktkorg();
        persistedKitchenfruktkorg.setId(1);
        persistedKitchenfruktkorg.setName("Köket");
        persistedKitchenfruktkorg.setLastChanged(Instant.now());

        Frukt persistedKiwi = new Frukt("Banan", 10, persistedKitchenfruktkorg);
        persistedKiwi.setId(1);

        persistedKitchenfruktkorg.getFruktList().add(persistedKiwi);

        Mockito.when(fruktkorgDAO.findById(1)).thenReturn(Optional.of(persistedKitchenfruktkorg));
        Mockito.when(fruktDAO.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(FruktMissingException.class, () -> fruktkorgService.restoreFruktkorgar(new ByteArrayInputStream(restoreExistingFruktkorgXML.getBytes())));

    }
}
