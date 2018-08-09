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

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.List;

public class FruktkorgTest {
    private static FruktkorgDAO fruktkorgDAO;
    private static FruktDAO fruktDAO;

    @BeforeEach
    public void init() {
        EntityManager entityManager = Persistence.createEntityManagerFactory("test").createEntityManager();

        fruktkorgDAO = new FruktkorgDAOImpl();
        fruktkorgDAO.setEntityManager(entityManager);

        fruktDAO = new FruktDAOImpl();
        fruktDAO.setEntityManager(entityManager);
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

        fruktkorg.setName("Super duper korg");

        fruktkorg = fruktkorgDAO.merge(fruktkorg);

        fruktkorgar = fruktkorgDAO.listFruktkorg();
        Assertions.assertEquals( "Super duper korg", fruktkorg.getName(), "Should return the correct name after merge");
        Assertions.assertEquals( "Super duper korg", fruktkorgar.get(0).getName(), "Should return the correct name after merge");
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
}
