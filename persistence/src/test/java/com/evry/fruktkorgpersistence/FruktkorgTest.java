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
import java.sql.Timestamp;
import java.util.List;

public class FruktkorgTest {
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
    public void saveAndReadFruktkorg() {
        final String NAME = "Test korg";
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName(NAME);

        fruktkorgDAO.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgDAO.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( NAME, fruktkorgar.get(0).getName(), "Name should be " + NAME);
        Assertions.assertEquals(1, fruktkorg.getId(), "Id of fruktkorg should be 1");
        Assertions.assertNotNull(fruktkorg.getLastChanged(), "Last Changed should be the set by default");
        Assertions.assertEquals(fruktkorg.getId(), fruktkorgar.get(0).getId(), "Id of fruktkorg should be 1");
    }

    @Test
    public void addFruktToFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg 2");

        Frukt superBanan = new Frukt("Super Banan", 1, fruktkorg);
        fruktkorg.getFruktList().add(superBanan);

        fruktkorgDAO.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgDAO.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( 1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");
    }

    @Test
    public void removeFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        fruktkorgDAO.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgDAO.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");

        fruktkorgDAO.remove(fruktkorg.getId());

        fruktkorgar = fruktkorgDAO.listFruktkorg();

        Assertions.assertEquals( 0, fruktkorgar.size(), "Should return 0 fruktkorgar after remove");
    }

    @Test
    public void mergeFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        fruktkorgDAO.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgDAO.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( "Test korg", fruktkorgar.get(0).getName(), "Should return the correct name");
        Assertions.assertNotNull(fruktkorg.getLastChanged(), "Last Changed should be the set by default");
        Timestamp firstChange = fruktkorg.getLastChanged();
        fruktkorg.setName("Super duper korg");

        fruktkorg = fruktkorgDAO.merge(fruktkorg);

        fruktkorgar = fruktkorgDAO.listFruktkorg();
        Assertions.assertEquals( "Super duper korg", fruktkorg.getName(), "Should return the correct name after merge");
        Assertions.assertEquals( "Super duper korg", fruktkorgar.get(0).getName(), "Should return the correct name after merge");
        Assertions.assertNotNull( fruktkorgar.get(0).getLastChanged(), "Last Changed should be the set by default");
        Assertions.assertNotEquals( firstChange, fruktkorgar.get(0).getLastChanged(), "Last changed should be updated when Fruktkorg changes ");
    }

    @Test
    public void mergeFruktkorgFruktList() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        Frukt superBanan = new Frukt("Super Banan", 1, fruktkorg);
        fruktkorg.getFruktList().add(superBanan);

        fruktkorgDAO.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgDAO.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( 1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");

        fruktkorg.getFruktList().remove(superBanan);

        fruktkorgDAO.merge(fruktkorg);
        fruktkorgar = fruktkorgDAO.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( 0, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 0 frukt");
        Assertions.assertEquals( 0, fruktDAO.listFrukt().size(), "All frukt should have been removed");
    }

    @Test
    void findFruktkorgByFruktType() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        Frukt superBanan = new Frukt("Super Banan", 1, fruktkorg);
        fruktkorg.getFruktList().add(superBanan);

        fruktkorgDAO.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgDAO.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( 1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");

        List<Fruktkorg> fruktkorgList = fruktkorgDAO.findFruktkorgByFrukt("Super Banan");

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

        fruktkorgDAO.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgDAO.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( 1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");

        List<Fruktkorg> fruktkorgList = fruktkorgDAO.findFruktkorgByFrukt("Vanlig Banan");

        Assertions.assertEquals(0, fruktkorgList.size());
    }
}
