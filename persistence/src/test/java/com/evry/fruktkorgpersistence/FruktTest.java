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

public class FruktTest {

    private static FruktkorgDAO fruktkorgDAO; 
    private static FruktDAO fruktDAO;

    @BeforeEach
    public void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test");

        fruktkorgDAO = new FruktkorgDAOImpl();
        fruktkorgDAO.setEntityManagerFactory(entityManagerFactory);
        
        fruktDAO = new FruktDAOImpl();
        fruktDAO.setEntityManagerFactory(entityManagerFactory);
    }

    @Test
    public void saveAndReadFrukt() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgDAO.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktDAO.persist(frukt);
        fruktkorgDAO.refresh(fruktkorg);

        Assertions.assertEquals(1, fruktDAO.listFrukt().size(), "Should return 1 frukt");
        Assertions.assertEquals(1, fruktkorgDAO.listFruktkorg().size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(1, fruktkorgDAO.listFruktkorg().get(0).getFruktList().size(), "Should be 1 frukt in fruktkorgen");
    }

    @Test
    public void saveReadAndEditFrukt() {
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
    public void moveFruktToAnotherFruktkorg() {
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
}
