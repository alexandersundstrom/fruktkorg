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
import java.time.Instant;
import java.util.List;

public class FruktkorgTest {
    private static FruktkorgRepositoryHibernate fruktkorgRepositoryHibernate;
    private static FruktRepositoryHibernate fruktRepositoryHibernate;

    @BeforeEach
    public void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test");

        fruktkorgRepositoryHibernate = new FruktkorgRepositoryHibernate();
        fruktkorgRepositoryHibernate.setEntityManagerFactory(entityManagerFactory);

        fruktRepositoryHibernate = new FruktRepositoryHibernate();
        fruktRepositoryHibernate.setEntityManagerFactory(entityManagerFactory);
    }

    @Test
    public void saveAndReadFruktkorg() {
        final String NAME = "Test korg";
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName(NAME);

        fruktkorgRepositoryHibernate.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgRepositoryHibernate.findAll();

        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(NAME, fruktkorgar.get(0).getName(), "Name should be " + NAME);
        Assertions.assertEquals(1, fruktkorg.getId(), "Id of fruktkorg should be 1");
        Assertions.assertNull(fruktkorg.getLastChanged(), "Last Changed should only be set when Frukts are added");
        Assertions.assertEquals(fruktkorg.getId(), fruktkorgar.get(0).getId(), "Id of fruktkorg should be 1");
    }

    @Test
    public void addFruktToFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg 2");

        Frukt superBanan = new Frukt("Super Banan", 1, fruktkorg);
        fruktkorg.getFruktList().add(superBanan);

        fruktkorgRepositoryHibernate.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgRepositoryHibernate.findAll();

        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");
        Assertions.assertNotNull(fruktkorg.getLastChanged(), "Last Changed should be the set by default");
    }

    @Test
    public void removeFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        fruktkorgRepositoryHibernate.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgRepositoryHibernate.findAll();

        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg");

        fruktkorgRepositoryHibernate.remove(fruktkorg.getId());

        fruktkorgar = fruktkorgRepositoryHibernate.findAll();

        Assertions.assertEquals(0, fruktkorgar.size(), "Should return 0 fruktkorgar after remove");
    }

    @Test
    public void removeByDateBefore() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test 1");
        fruktkorg.setLastChanged(Instant.now());
        fruktkorgRepositoryHibernate.persist(fruktkorg);

        Instant oneObjectCreated = Instant.now();

        Fruktkorg fruktkorg2 = new Fruktkorg();
        fruktkorg2.setName("Test 2");
        fruktkorg2.setLastChanged(Instant.now());
        fruktkorgRepositoryHibernate.persist(fruktkorg2);

        List<Fruktkorg> fruktkorgar = fruktkorgRepositoryHibernate.findAll();
        Assertions.assertEquals(2, fruktkorgar.size(), "Should return 2 fruktkorgar before deletion");
        fruktkorgRepositoryHibernate.removeAllBefore(oneObjectCreated);
        fruktkorgar = fruktkorgRepositoryHibernate.findAll();
        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg after deletion");
    }

    @Test
    public void removeByDateBeforeWhenDateIsTheSame() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test 1");
        Instant now = Instant.now();

        fruktkorg.setLastChanged(now);
        fruktkorgRepositoryHibernate.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgRepositoryHibernate.findAll();
        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg before deletion");
        fruktkorgRepositoryHibernate.removeAllBefore(now);
        fruktkorgar = fruktkorgRepositoryHibernate.findAll();
        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg after deletion");
    }

    @Test
    public void mergeFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        fruktkorgRepositoryHibernate.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgRepositoryHibernate.findAll();

        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals("Test korg", fruktkorgar.get(0).getName(), "Should return the correct name");
        fruktkorg.setName("Super duper korg");

        fruktkorg = fruktkorgRepositoryHibernate.merge(fruktkorg);

        fruktkorgar = fruktkorgRepositoryHibernate.findAll();
        Assertions.assertEquals("Super duper korg", fruktkorg.getName(), "Should return the correct name after merge");
        Assertions.assertEquals("Super duper korg", fruktkorgar.get(0).getName(), "Should return the correct name after merge");
        Assertions.assertNull(fruktkorgar.get(0).getLastChanged(), "Last Changed should only be set when Frukts are added");
    }

    @Test
    public void mergeFruktkorgFruktList() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        Frukt superBanan = new Frukt("Super Banan", 1, fruktkorg);
        fruktkorg.getFruktList().add(superBanan);

        fruktkorgRepositoryHibernate.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgRepositoryHibernate.findAll();

        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");

        fruktkorg.getFruktList().remove(superBanan);

        fruktkorgRepositoryHibernate.merge(fruktkorg);
        fruktkorgar = fruktkorgRepositoryHibernate.findAll();

        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(0, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 0 frukt");
        Assertions.assertEquals(0, fruktRepositoryHibernate.findAll().size(), "All frukt should have been removed");
    }

    @Test
    void findFruktkorgByFruktType() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        Frukt superBanan = new Frukt("Super Banan", 1, fruktkorg);
        fruktkorg.getFruktList().add(superBanan);

        fruktkorgRepositoryHibernate.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgRepositoryHibernate.findAll();

        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");

        List<Fruktkorg> fruktkorgList = fruktkorgRepositoryHibernate.findAllByFruktType("Super Banan");

        Assertions.assertEquals(1, fruktkorgList.size());
        Fruktkorg searchFruktkorg = fruktkorgList.get(0);
        Assertions.assertEquals(1, searchFruktkorg.getId());
        Assertions.assertEquals(1, searchFruktkorg.getFruktList().size());
        Assertions.assertEquals("Super Banan", searchFruktkorg.getFruktList().get(0).getType());
    }

    @Test
    void findFruktkorgByWrongFruktType() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        Frukt superBanan = new Frukt("Super Banan", 1, fruktkorg);
        fruktkorg.getFruktList().add(superBanan);

        fruktkorgRepositoryHibernate.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgRepositoryHibernate.findAll();

        Assertions.assertEquals(1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals(1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");

        List<Fruktkorg> fruktkorgList = fruktkorgRepositoryHibernate.findAllByFruktType("Vanlig Banan");

        Assertions.assertEquals(0, fruktkorgList.size());
    }
}
