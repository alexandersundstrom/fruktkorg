package com.evry.fruktkorgpersistence;

import com.evry.fruktkorgpersistence.hibernate.FruktRepositoryHibernate;
import com.evry.fruktkorgpersistence.hibernate.FruktkorgRepositoryHibernate;
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

    private static FruktkorgRepositoryHibernate fruktkorgRepository;
    private static FruktRepositoryHibernate fruktRepository;

    @BeforeEach
    void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test");

        fruktkorgRepository = new FruktkorgRepositoryHibernate();
        fruktkorgRepository.setEntityManagerFactory(entityManagerFactory);

        fruktRepository = new FruktRepositoryHibernate();
        fruktRepository.setEntityManagerFactory(entityManagerFactory);
    }

    @Test
    void saveAndReadFrukt() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepository.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepository.persist(frukt);
        fruktkorgRepository.refresh(fruktkorg);

        Assertions.assertEquals(1, fruktRepository.findAll().size(), "Should return 1 frukt");
        Assertions.assertEquals(1, fruktkorgRepository.findAll().size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(1, fruktkorgRepository.findAll().get(0).getFruktList().size(), "Should be 1 frukt in fruktkorgen");
    }

    @Test
    void saveAndReadFruktWithId() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepository.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepository.persist(frukt);
        fruktkorgRepository.refresh(fruktkorg);

        Assertions.assertTrue(fruktRepository.findById(frukt.getId()).isPresent(), "Frukt should be found by id");
    }

    @Test
    void readFruktWereIdIsNotPresent() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepository.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepository.persist(frukt);
        fruktkorgRepository.refresh(fruktkorg);

        Assertions.assertFalse(fruktRepository.findById(2).isPresent(), "Frukt should not be found by");
    }

    @Test
    void saveReadAndEditFrukt() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepository.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepository.persist(frukt);

        Assertions.assertEquals(1, fruktRepository.findAll().size(), "Should return 1 frukt");

        frukt.setAmount(2);

        fruktRepository.merge(frukt);

        Assertions.assertEquals(1, fruktRepository.findAll().size(), "Should return 1 frukt");
        Assertions.assertEquals(2, fruktRepository.findAll().get(0).getAmount(), "Amount of Äpplen should be 2");
    }

    @Test
    void moveFruktToAnotherFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg("Test Korg");
        fruktkorgRepository.persist(fruktkorg);

        Frukt frukt = new Frukt("Äpple", 3, fruktkorg);

        fruktRepository.persist(frukt);

        Assertions.assertEquals(1, fruktRepository.findAll().size(), "Should return 1 frukt");

        Fruktkorg fruktkorg2 = new Fruktkorg("Test Korg 2");
        fruktkorgRepository.persist(fruktkorg2);

        frukt.setFruktkorg(fruktkorg2);

        fruktRepository.merge(frukt);

        Assertions.assertEquals(1, fruktRepository.findAll().size(), "Should return 1 frukt");
        Assertions.assertEquals(fruktkorg2.getId(), fruktRepository.findAll().get(0).getFruktkorg().getId(), "Fruktkorg should have changed");
    }

    @Test
    void listUniqueFruktTypes() {
        Fruktkorg fruktkorg1 = new Fruktkorg("Test Korg 1");
        fruktkorgRepository.persist(fruktkorg1);

        Fruktkorg fruktkorg2 = new Fruktkorg("Test Korg 2");
        fruktkorgRepository.persist(fruktkorg2);

        Frukt frukt1 = new Frukt("Äpple", 3, fruktkorg1);
        fruktRepository.persist(frukt1);

        Frukt frukt2 = new Frukt("Äpple", 3, fruktkorg2);
        fruktRepository.persist(frukt2);

        Frukt frukt3 = new Frukt("Banan", 3, fruktkorg2);
        fruktRepository.persist(frukt3);

        List<String> fruktTypes = fruktRepository.findAllUniqueFruktTypes();
        Assertions.assertEquals(2, fruktTypes.size());
        Assertions.assertIterableEquals(Arrays.asList("Banan", "Äpple"), fruktTypes);
    }
}
