package com.evry.fruktkorgservice;

import com.evry.fruktkorg.domain.model.Frukt;
import com.evry.fruktkorg.domain.model.Fruktkorg;
import com.evry.fruktkorgpersistence.hibernate.FruktRepositoryHibernate;
import com.evry.fruktkorgpersistence.hibernate.FruktkorgRepositoryHibernate;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktBuilder;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.model.ImmutableFruktkorgBuilder;
import com.evry.fruktkorgservice.exception.FruktMissingException;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
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

    private FruktkorgRepositoryHibernate fruktkorgRepository;
    private FruktRepositoryHibernate fruktRepository;
    private FruktkorgService fruktkorgService;

    private static final String UPDATE_XML = "updateFruktkorgar.xml";
    private static final String RESTORE_EXISTING_XML = "restoreExistingFruktkorg.xml";
    private static final String RESTORE_NEW_XML = "restoreNewFruktkorg.xml";


    @BeforeEach
    void init() {
        fruktkorgRepository = Mockito.mock(FruktkorgRepositoryHibernate.class);
        fruktRepository = Mockito.mock(FruktRepositoryHibernate.class);
        fruktkorgService = new FruktkorgService(fruktkorgRepository, fruktRepository);
    }

    @Test
    void createFruktkorg() {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg) arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Korg");

            return null;
        }).when(fruktkorgRepository).persist(Mockito.any(Fruktkorg.class));

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
        ImmutableFrukt fruktToBeCreated = new ImmutableFruktBuilder().setType("Banan")
                .setAmount(5)
                .createImmutableFrukt();

        ImmutableFruktkorg fruktkorgToBeCreated = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .addFrukt(fruktToBeCreated)
                .createImmutableFruktkorg();

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

        }).when(fruktkorgRepository).persist(Mockito.any(Fruktkorg.class));

        ImmutableFruktkorg createdFruktkorg = fruktkorgService.createFruktkorg(fruktkorgToBeCreated);

        Assertions.assertEquals(1, createdFruktkorg.getId(), "Id should be set to one");
        Assertions.assertEquals("Korg", createdFruktkorg.getName(), "Name should be Korg");
        Assertions.assertEquals(1, createdFruktkorg.getFruktList().size(), "Should be one Frukt in Fruktkorg");
        Assertions.assertNotNull(createdFruktkorg.getLastChanged(), "Last Change should be set when at least 1 Frukt is provided");
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
        }).when(fruktkorgRepository).persist(Mockito.any(Fruktkorg.class));

        Fruktkorg mockedPersistedFruktkorg = new Fruktkorg();
        mockedPersistedFruktkorg.setName("Korg");
        mockedPersistedFruktkorg.setId(1);

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

        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.of(mockedPersistedFruktkorg));
        Mockito.when(fruktkorgRepository.merge(Mockito.any(Fruktkorg.class))).thenReturn(mockedPersistedFruktkorg);
        persistedFruktkorg = fruktkorgService.addAllFrukterToFruktkorg(persistedFruktkorg.getId(), fruktToAdd);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one");
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg");
        Assertions.assertEquals(1, persistedFruktkorg.getFruktList().size(), "Frukt in fruktkorg should be 1");
        Assertions.assertNotNull(persistedFruktkorg.getLastChanged(), "Last Change should be set when at least 1 Frukt is provided");
    }

    @Test
    void addFruktToMissingFruktkorg() {
        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.empty());

        ImmutableFrukt fruktToAdd = new ImmutableFruktBuilder()
                .setType("banan")
                .setAmount(50)
                .setFruktkorgId(1)
                .createImmutableFrukt();

        Assertions.assertThrows(FruktkorgMissingException.class, () -> fruktkorgService.addAllFrukterToFruktkorg(1, fruktToAdd));
    }

    @Test
    void addFruktToFruktkorgWithFruktAlreadyExisting() throws FruktkorgMissingException {
        Fruktkorg returnFruktkorg = new Fruktkorg();
        returnFruktkorg.setName("Korg");
        returnFruktkorg.setId(1);

        Frukt returnFrukt = new Frukt("banan", 5, returnFruktkorg);
        returnFrukt.setId(1);
        returnFruktkorg.getFruktList().add(returnFrukt);
        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgRepository.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.addAllFrukterToFruktkorg(1, new ImmutableFruktBuilder()
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
        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgRepository.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

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
        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgRepository.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

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

        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.of(returnFruktkorg));
        Mockito.when(fruktkorgRepository.merge(Mockito.any(Fruktkorg.class))).thenReturn(returnFruktkorg);

        Assertions.assertThrows(FruktMissingException.class, () -> fruktkorgService.removeFruktFromFruktkorg(1, "banan", 5));
    }

    @Test
    void removeFruktFromMissingFruktkorg() {
        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.empty());

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

        Mockito.when(fruktkorgRepository.findAllByFruktType("Super Banan"))
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

        Mockito.when(fruktkorgRepository.findAllByFruktType("Super Banan"))
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

        Mockito.when(fruktkorgRepository.findAll())
                .thenReturn(Arrays.asList(fruktkorg1, fruktkorg2, fruktkorg3));

        List<ImmutableFruktkorg> fruktkorgList = fruktkorgService.listFruktkorgar();

        Assertions.assertEquals(3, fruktkorgList.size());
        Assertions.assertEquals("Korg 1", fruktkorgList.get(0).getName());
        Assertions.assertEquals("Korg 2", fruktkorgList.get(1).getName());
        Assertions.assertEquals("Korg 3", fruktkorgList.get(2).getName());
    }

    @Test
    void updateFruktkorgarFromXML() throws Exception {
        Fruktkorg mockedPersistedFruktkorg = new Fruktkorg();
        mockedPersistedFruktkorg.setId(1);
        mockedPersistedFruktkorg.setName("Korg 1");
        mockedPersistedFruktkorg.setLastChanged(Instant.now());

        Frukt frukt1 = new Frukt("Banan", 5, mockedPersistedFruktkorg);
        frukt1.setId(1);

        mockedPersistedFruktkorg.getFruktList().add(frukt1);

        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.of(mockedPersistedFruktkorg));
        Mockito.when(fruktkorgRepository.merge(mockedPersistedFruktkorg)).thenReturn(mockedPersistedFruktkorg);

        List<ImmutableFruktkorg> updatedFruktkorgar = fruktkorgService.updateFruktkorgar(getClass().getClassLoader().getResourceAsStream(UPDATE_XML));

        Assertions.assertEquals(mockedPersistedFruktkorg.getId(), updatedFruktkorgar.get(0).getId());
        Assertions.assertEquals(mockedPersistedFruktkorg.getName(), updatedFruktkorgar.get(0).getName());
        Assertions.assertEquals(2, updatedFruktkorgar.get(0).getFruktList().size());
    }

    @Test
    void restoreFruktkorgarFromXML() throws Exception {
        Fruktkorg mockedPersistedFruktkorg = new Fruktkorg();
        mockedPersistedFruktkorg.setId(1);
        mockedPersistedFruktkorg.setName("Köket");
        mockedPersistedFruktkorg.setLastChanged(Instant.now());

        Frukt mockedPersistedKiwi = new Frukt("Banan", 10, mockedPersistedFruktkorg);
        mockedPersistedKiwi.setId(1);

        mockedPersistedFruktkorg.getFruktList().add(mockedPersistedKiwi);

        Fruktkorg restoredFromXML = new Fruktkorg();
        restoredFromXML.setName("Köket");
        restoredFromXML.setId(1);

        Frukt kiwiRestored = new Frukt("Kiwi", 10, restoredFromXML);
        kiwiRestored.setId(1);

        Frukt apelsinRestored = new Frukt("Apelsin", 10, restoredFromXML);
        apelsinRestored.setId(2);

        restoredFromXML.getFruktList().add(kiwiRestored);
        restoredFromXML.getFruktList().add(apelsinRestored);

        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.of(mockedPersistedFruktkorg));
        Mockito.when(fruktRepository.findById(1)).thenReturn(Optional.of(mockedPersistedKiwi));
        Mockito.when(fruktkorgRepository.merge(Mockito.any())).thenReturn(restoredFromXML);

        List<ImmutableFruktkorg> restoredFruktkorgar = fruktkorgService.restoreFruktkorgar(getClass().getClassLoader().getResourceAsStream(RESTORE_EXISTING_XML));

        Assertions.assertEquals(1, restoredFruktkorgar.size(), "Should only be one Fruktkorg");
        Assertions.assertEquals(2, restoredFruktkorgar.get(0).getFruktList().size(), "Should only be two Frukter");
        Assertions.assertEquals("Köket", restoredFruktkorgar.get(0).getName());

    }

    @Test
    void createFruktkorgarFromXML() throws Exception {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg) arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Köket");
            fruktkorg.getFruktList().clear();

            Frukt kiwi = new Frukt("Kiwi", 10, fruktkorg);
            kiwi.setId(1);

            Frukt apelsin = new Frukt("Apelsin", 10, fruktkorg);
            apelsin.setId(2);
            fruktkorg.getFruktList().add(kiwi);
            fruktkorg.getFruktList().add(apelsin);

            return null;
        }).when(fruktkorgRepository).persist(Mockito.any(Fruktkorg.class));

        List<ImmutableFruktkorg> restoredFruktkorgar = fruktkorgService.restoreFruktkorgar(getClass().getClassLoader().getResourceAsStream(RESTORE_NEW_XML));
        Assertions.assertEquals(1, restoredFruktkorgar.size(), "Should only be one Fruktkorg");
        Assertions.assertEquals(2, restoredFruktkorgar.get(0).getFruktList().size(), "Should only be two Frukter");

    }

    @Test
    void restoreWithFruktkorgIdNotFound() {
        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(FruktkorgMissingException.class, () -> fruktkorgService.updateFruktkorgar(getClass().getClassLoader().getResourceAsStream(UPDATE_XML)));

    }

    @Test
    void updateWithFruktkorgIdNotFound() {
        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(FruktkorgMissingException.class, () -> fruktkorgService.restoreFruktkorgar(getClass().getClassLoader().getResourceAsStream(RESTORE_EXISTING_XML)));
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

        Mockito.when(fruktkorgRepository.findById(1)).thenReturn(Optional.of(persistedKitchenfruktkorg));
        Mockito.when(fruktRepository.findById(1)).thenReturn(Optional.empty());
        Assertions.assertThrows(FruktMissingException.class, () -> fruktkorgService.restoreFruktkorgar(getClass().getClassLoader().getResourceAsStream(RESTORE_EXISTING_XML)));

    }
}
