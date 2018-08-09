package com.evry.fruktkorgpersistence;

import com.evry.fruktkorgpersistence.dao.FruktDAO;
import com.evry.fruktkorgpersistence.dao.FruktDAOImpl;
import com.evry.fruktkorgpersistence.dao.FruktkorgDAO;
import com.evry.fruktkorgpersistence.dao.FruktkorgDAOImpl;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgpersistence.service.FruktService;
import com.evry.fruktkorgpersistence.service.FruktServiceImpl;
import com.evry.fruktkorgpersistence.service.FruktkorgService;
import com.evry.fruktkorgpersistence.service.FruktkorgServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class FruktTest {

    private static FruktkorgService fruktkorgService;
    private static FruktService fruktService;

    @BeforeEach
    public void init() {
        EntityManager entityManager = Persistence.createEntityManagerFactory("test").createEntityManager();

        FruktkorgDAO fruktkorgDAO = new FruktkorgDAOImpl();
        fruktkorgDAO.setEntityManager(entityManager);

        fruktkorgService = new FruktkorgServiceImpl(fruktkorgDAO);

        FruktDAO fruktDAO = new FruktDAOImpl();
        fruktDAO.setEntityManager(entityManager);

        fruktService = new FruktServiceImpl(fruktDAO);
    }

    @Test
    public void saveFruktAndRead() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgService.persist(fruktkorg);

        Frukt frukt = new Frukt("äpple", 3, fruktkorg);

        fruktService.persist(frukt);
        fruktkorgService.refresh(fruktkorg);

        Assertions.assertEquals(1, fruktService.listFrukt().size());
        Assertions.assertEquals(1, fruktkorgService.listFruktkorg().size());
        Assertions.assertEquals(1, fruktkorgService.listFruktkorg().get(0).getFruktList().size(), "Should be 1 frukt in fruktkorgen");
    }

    @Test
    public void saveFruktAndReadEdit() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgService.persist(fruktkorg);

        Frukt frukt = new Frukt("äpple", 3, fruktkorg);

        fruktService.persist(frukt);

        Assertions.assertEquals(1, fruktService.listFrukt().size());

        frukt.setAmount(2);

        fruktService.merge(frukt);

        Assertions.assertEquals(1, fruktService.listFrukt().size());
        Assertions.assertEquals(2, fruktService.listFrukt().get(0).getAmount());
    }

    @Test
    public void moveFruktToAnotherFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgService.persist(fruktkorg);

        Frukt frukt = new Frukt("äpple", 3, fruktkorg);

        fruktService.persist(frukt);

        Assertions.assertEquals(1, fruktService.listFrukt().size());

        Fruktkorg fruktkorg2 = new Fruktkorg("Test Korg 2");
        fruktkorgService.persist(fruktkorg2);

        frukt.setFruktkorg(fruktkorg2);

        fruktService.merge(frukt);

        Assertions.assertEquals(1, fruktService.listFrukt().size());
        Assertions.assertEquals(fruktkorg2.getId(), fruktService.listFrukt().get(0).getFruktkorg().getId());
    }
}
