package com.evry.fruktkorgpersistence;

import com.evry.fruktkorgpersistence.dao.FruktDAO;
import com.evry.fruktkorgpersistence.dao.FruktDAOImpl;
import com.evry.fruktkorgpersistence.dao.FruktkorgDAO;
import com.evry.fruktkorgpersistence.dao.FruktkorgDAOImpl;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Arrays;
import java.util.List;

class FruktTest {

    private static FruktkorgDAO fruktkorgDAO; 
    private static FruktDAO fruktDAO;

    @BeforeEach
    void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test");

        fruktkorgDAO = new FruktkorgDAOImpl();
        fruktkorgDAO.setEntityManagerFactory(entityManagerFactory);
        
        fruktDAO = new FruktDAOImpl();
        fruktDAO.setEntityManagerFactory(entityManagerFactory);
    }

    @Test
    void saveAndReadFrukt() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgDAO.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktDAO.persist(frukt);
        fruktkorgDAO.refresh(fruktkorg);

        Assertions.assertEquals(1, fruktDAO.listFrukt().size(), "Should return 1 frukt");
        Assertions.assertEquals(1, fruktkorgDAO.listFruktkorgar().size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(1, fruktkorgDAO.listFruktkorgar().get(0).getFruktList().size(), "Should be 1 frukt in fruktkorgen");
    }

    @Test
    void saveAndReadFruktWithId() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgDAO.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktDAO.persist(frukt);
        fruktkorgDAO.refresh(fruktkorg);

        Assertions.assertTrue(fruktDAO.findFruktById(frukt.getId()).isPresent(), "Frukt should be found by id");
    }

    @Test
    void readFruktWereIdIsNotPresent() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgDAO.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktDAO.persist(frukt);
        fruktkorgDAO.refresh(fruktkorg);

        Assertions.assertFalse(fruktDAO.findFruktById(2).isPresent(), "Frukt should not be found by");
    }

    @Test
    void saveReadAndEditFrukt() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgDAO.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktDAO.persist(frukt);

        Assertions.assertEquals(1, fruktDAO.listFrukt().size(), "Should return 1 frukt");

        frukt.setAmount(2);

        fruktDAO.merge(frukt);

        Assertions.assertEquals(1, fruktDAO.listFrukt().size(),"Should return 1 frukt");
        Assertions.assertEquals(2, fruktDAO.listFrukt().get(0).getAmount(), "Amount of Äpplen should be 2");
    }

    @Test
    void moveFruktToAnotherFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgDAO.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktDAO.persist(frukt);

        Assertions.assertEquals(1, fruktDAO.listFrukt().size(), "Should return 1 frukt");

        Fruktkorg fruktkorg2 = new Fruktkorg("Test Korg 2");
        fruktkorgDAO.persist(fruktkorg2);

        frukt.setFruktkorg(fruktkorg2);

        fruktDAO.merge(frukt);

        Assertions.assertEquals(1, fruktDAO.listFrukt().size(), "Should return 1 frukt");
        Assertions.assertEquals(fruktkorg2.getId(), fruktDAO.listFrukt().get(0).getFruktkorg().getId(), "Fruktkorg should have changed");
    }

    @Test
    void listUniqueFruktTypes() {
        Fruktkorg fruktkorg1 = new Fruktkorg("Test Korg 1");
        fruktkorgDAO.persist(fruktkorg1);

        Fruktkorg fruktkorg2 = new Fruktkorg("Test Korg 2");
        fruktkorgDAO.persist(fruktkorg2);

        Frukt frukt1 = new Frukt("Äpple", 3, fruktkorg1);
        fruktDAO.persist(frukt1);

        Frukt frukt2 = new Frukt("Äpple", 3, fruktkorg2);
        fruktDAO.persist(frukt2);

        Frukt frukt3 = new Frukt("Banan", 3, fruktkorg2);
        fruktDAO.persist(frukt3);

        List<String> fruktTypes = fruktDAO.listUniqueFruktTypes();
        Assertions.assertEquals(2, fruktTypes.size());
        Assertions.assertIterableEquals(Arrays.asList("Banan", "Äpple"), fruktTypes);
    }
}
