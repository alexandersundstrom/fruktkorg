package com.evry.fruktkorgpersistence;

import com.evry.fruktkorgpersistence.dao.FruktRepositoryHibernate;
import com.evry.fruktkorgpersistence.dao.FruktkorgRepositoryHibernate;
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

    private static FruktkorgRepositoryHibernate fruktkorgRepositoryHibernate;
    private static FruktRepositoryHibernate fruktRepositoryHibernate;

    @BeforeEach
    void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test");

        fruktkorgRepositoryHibernate = new FruktkorgRepositoryHibernate();
        fruktkorgRepositoryHibernate.setEntityManagerFactory(entityManagerFactory);

        fruktRepositoryHibernate = new FruktRepositoryHibernate();
        fruktRepositoryHibernate.setEntityManagerFactory(entityManagerFactory);
    }

    @Test
    void saveAndReadFrukt() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepositoryHibernate.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepositoryHibernate.persist(frukt);
        fruktkorgRepositoryHibernate.refresh(fruktkorg);

        Assertions.assertEquals(1, fruktRepositoryHibernate.findAll().size(), "Should return 1 frukt");
        Assertions.assertEquals(1, fruktkorgRepositoryHibernate.findAll().size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(1, fruktkorgRepositoryHibernate.findAll().get(0).getFruktList().size(), "Should be 1 frukt in fruktkorgen");
    }

    @Test
    void saveAndReadFruktWithId() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepositoryHibernate.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepositoryHibernate.persist(frukt);
        fruktkorgRepositoryHibernate.refresh(fruktkorg);

        Assertions.assertTrue(fruktRepositoryHibernate.findById(frukt.getId()).isPresent(), "Frukt should be found by id");
    }

    @Test
    void readFruktWereIdIsNotPresent() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepositoryHibernate.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepositoryHibernate.persist(frukt);
        fruktkorgRepositoryHibernate.refresh(fruktkorg);

        Assertions.assertFalse(fruktRepositoryHibernate.findById(2).isPresent(), "Frukt should not be found by");
    }

    @Test
    void saveReadAndEditFrukt() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepositoryHibernate.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepositoryHibernate.persist(frukt);

        Assertions.assertEquals(1, fruktRepositoryHibernate.findAll().size(), "Should return 1 frukt");

        frukt.setAmount(2);

        fruktRepositoryHibernate.merge(frukt);

        Assertions.assertEquals(1, fruktRepositoryHibernate.findAll().size(), "Should return 1 frukt");
        Assertions.assertEquals(2, fruktRepositoryHibernate.findAll().get(0).getAmount(), "Amount of Äpplen should be 2");
    }

    @Test
    void moveFruktToAnotherFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepositoryHibernate.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepositoryHibernate.persist(frukt);

        Assertions.assertEquals(1, fruktRepositoryHibernate.findAll().size(), "Should return 1 frukt");

        Fruktkorg fruktkorg2 = new Fruktkorg("Test Korg 2");
        fruktkorgRepositoryHibernate.persist(fruktkorg2);

        frukt.setFruktkorg(fruktkorg2);

        fruktRepositoryHibernate.merge(frukt);

        Assertions.assertEquals(1, fruktRepositoryHibernate.findAll().size(), "Should return 1 frukt");
        Assertions.assertEquals(fruktkorg2.getId(), fruktRepositoryHibernate.findAll().get(0).getFruktkorg().getId(), "Fruktkorg should have changed");
    }

    @Test
    void listUniqueFruktTypes() {
        Fruktkorg fruktkorg1 = new Fruktkorg("Test Korg 1");
        fruktkorgRepositoryHibernate.persist(fruktkorg1);

        Fruktkorg fruktkorg2 = new Fruktkorg("Test Korg 2");
        fruktkorgRepositoryHibernate.persist(fruktkorg2);

        Frukt frukt1 = new Frukt("Äpple", 3, fruktkorg1);
        fruktRepositoryHibernate.persist(frukt1);

        Frukt frukt2 = new Frukt("Äpple", 3, fruktkorg2);
        fruktRepositoryHibernate.persist(frukt2);

        Frukt frukt3 = new Frukt("Banan", 3, fruktkorg2);
        fruktRepositoryHibernate.persist(frukt3);

        List<String> fruktTypes = fruktRepositoryHibernate.findAllUniqueFruktTypes();
        Assertions.assertEquals(2, fruktTypes.size());
        Assertions.assertIterableEquals(Arrays.asList("Banan", "Äpple"), fruktTypes);
    }
}
