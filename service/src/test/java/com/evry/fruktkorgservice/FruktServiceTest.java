package com.evry.fruktkorgservice;

import com.evry.fruktkorgpersistence.dao.FruktDAO;
import com.evry.fruktkorgservice.service.FruktService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

class FruktServiceTest {
    private FruktDAO fruktDAO;
    private FruktService fruktService;

    @BeforeEach
    void init() {
        fruktDAO = Mockito.mock(FruktDAO.class);
        fruktService = new FruktService(fruktDAO);
    }

    @Test
    void getUniqueFruktTypes() {
        Mockito.when(fruktDAO.listUniqueFruktTypes()).thenReturn(Arrays.asList("Banan", "Äpple"));

        List<String> uniqueFruktTypes = fruktService.getUniqueFruktTypes();

        Assertions.assertEquals(2, uniqueFruktTypes.size());
        Assertions.assertIterableEquals(Arrays.asList("Banan", "Äpple"), uniqueFruktTypes);
    }
}
