package com.evry.fruktkorgpersistence;

import com.evry.fruktkorg.domain.model.Frukt;
import com.evry.fruktkorg.domain.model.Fruktkorg;
import com.evry.fruktkorgpersistence.hibernate.FruktRepositoryHibernate;
import com.evry.fruktkorgpersistence.hibernate.FruktkorgRepositoryHibernate;
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
    void listUniqueFruktTypes() {
        Fruktkorg fruktkorg1 = new Fruktkorg("Test Korg 1");
        Frukt apple = new Frukt("Äpple", 3, fruktkorg1);
        fruktkorg1.getFruktList().add(apple);
        fruktkorgRepository.persist(fruktkorg1);

        Fruktkorg fruktkorg2 = new Fruktkorg("Test Korg 2");
        Frukt apple2 = new Frukt("Äpple", 3, fruktkorg2);
        Frukt banana = new Frukt("Banan", 3, fruktkorg2);
        fruktkorg2.getFruktList().add(apple2);
        fruktkorg2.getFruktList().add(banana);
        fruktkorgRepository.persist(fruktkorg2);

        List<String> fruktTypes = fruktRepository.findAllUniqueFruktTypes();
        Assertions.assertEquals(2, fruktTypes.size());
        Assertions.assertIterableEquals(Arrays.asList("Banan", "Äpple"), fruktTypes);
    }
}
