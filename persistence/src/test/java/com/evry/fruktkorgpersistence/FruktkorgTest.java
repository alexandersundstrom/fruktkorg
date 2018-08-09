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
import java.util.List;

public class FruktkorgTest {
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
    public void saveFruktkorgAndRead() {
        final String NAME = "Test korg";
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName(NAME);

        fruktkorgService.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgService.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( NAME, fruktkorgar.get(0).getName(), "Name should be " + NAME);
        Assertions.assertEquals(1, fruktkorg.getId());
        Assertions.assertEquals(fruktkorg.getId(), fruktkorgar.get(0).getId());
    }

    @Test
    public void addFruktToFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg 2");

        Frukt superBanan = new Frukt("Super Banan", 1, fruktkorg);
        fruktkorg.getFruktList().add(superBanan);

        fruktkorgService.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgService.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( 1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");
    }

    @Test
    public void removeFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        fruktkorgService.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgService.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");

        fruktkorgService.remove(fruktkorg);

        fruktkorgar = fruktkorgService.listFruktkorg();

        Assertions.assertEquals( 0, fruktkorgar.size(), "Should return 0 fruktkorgar after remove");
    }

    @Test
    public void mergeFruktkorg() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        fruktkorgService.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgService.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( "Test korg", fruktkorgar.get(0).getName(), "Should return the correct name");

        fruktkorg.setName("Super duper korg");

        fruktkorg = fruktkorgService.merge(fruktkorg);

        fruktkorgar = fruktkorgService.listFruktkorg();
        Assertions.assertEquals( "Super duper korg", fruktkorg.getName(), "Should return the correct name after merge");
        Assertions.assertEquals( "Super duper korg", fruktkorgar.get(0).getName(), "Should return the correct name after merge");
    }

    @Test
    public void mergeFruktkorgFruktList() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");

        Frukt superBanan = new Frukt("Super Banan", 1, fruktkorg);
        fruktkorg.getFruktList().add(superBanan);

        fruktkorgService.persist(fruktkorg);

        List<Fruktkorg> fruktkorgar = fruktkorgService.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( 1, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 1 frukt");

        fruktkorg.getFruktList().remove(superBanan);

        fruktkorgService.merge(fruktkorg);
        fruktkorgar = fruktkorgService.listFruktkorg();

        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
        Assertions.assertEquals( 0, fruktkorgar.get(0).getFruktList().size(), "fruktkorgen should have 0 frukt");
        Assertions.assertEquals( 0, fruktService.listFrukt().size(), "All frukt should have been removed");
    }
}
