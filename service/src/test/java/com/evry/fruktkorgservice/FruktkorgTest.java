package com.evry.fruktkorgservice;

import com.evry.fruktkorgpersistence.dao.FruktDAO;
import com.evry.fruktkorgpersistence.dao.FruktkorgDAO;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktBuilder;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.model.ImmutableFruktkorgBuilder;
import com.evry.fruktkorgservice.service.FruktService;
import com.evry.fruktkorgservice.service.FruktServiceImpl;
import com.evry.fruktkorgservice.service.FruktkorgService;
import com.evry.fruktkorgservice.service.FruktkorgServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class FruktkorgTest {

    private FruktDAO fruktDAO;
    private FruktkorgDAO fruktkorgDAO;
    private FruktkorgService fruktkorgService;
    private FruktService fruktService;

    @BeforeEach
    public void init() {
        fruktDAO = Mockito.mock(FruktDAO.class);
        fruktkorgDAO = Mockito.mock(FruktkorgDAO.class);
        fruktService = new FruktServiceImpl(fruktDAO);
        fruktkorgService = new FruktkorgServiceImpl(fruktkorgDAO);
    }

    @Test
    public void createFruktkorg() {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg)arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Korg");

            return null;
        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .createImmutableFruktkorg();

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
    }

    @Test
    public void createFruktkorgWithFrukt() {
        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Fruktkorg fruktkorg = (Fruktkorg)arguments[0];
            fruktkorg.setId(1);
            fruktkorg.setName("Korg");

            Frukt frukt = new Frukt();
            frukt.setId(1);
            frukt.setType("Banan");
            frukt.setAmount(5);
            frukt.setFruktkorg(fruktkorg);

            fruktkorg.getFruktList().clear();
            fruktkorg.getFruktList().add(frukt);
            return null;

        }).when(fruktkorgDAO).persist(Mockito.any(Fruktkorg.class));

        ImmutableFrukt immutableFrukt = new ImmutableFruktBuilder().setType("Banan")
                .setAmount(5)
                .createImmutableFrukt();

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .addFrukt(immutableFrukt)
                .createImmutableFruktkorg();

        ImmutableFruktkorg persistedFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        Assertions.assertEquals(1, persistedFruktkorg.getId(), "Id should be set to one" );
        Assertions.assertEquals("Korg", persistedFruktkorg.getName(), "Name should be Korg" );
        Assertions.assertEquals(1, persistedFruktkorg.getFruktList().size(), "Should be one Frukt in Fruktkorg" );
    }
}
