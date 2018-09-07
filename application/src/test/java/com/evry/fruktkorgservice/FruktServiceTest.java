package com.evry.fruktkorgservice;

import com.evry.fruktkorgpersistence.hibernate.FruktRepositoryHibernate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

class FruktServiceTest {
    private FruktRepositoryHibernate fruktRepository;
    private FruktService fruktService;

    @BeforeEach
    void init() {
        fruktRepository = Mockito.mock(FruktRepositoryHibernate.class);
        fruktService = new FruktService(fruktRepository);
    }

    @Test
    void getUniqueFruktTypes() {
        Mockito.when(fruktRepository.findAllUniqueFruktTypes()).thenReturn(Arrays.asList("Banan", "Äpple"));

        List<String> uniqueFruktTypes = fruktService.getUniqueFruktTypes();

        Assertions.assertEquals(2, uniqueFruktTypes.size());
        Assertions.assertIterableEquals(Arrays.asList("Banan", "Äpple"), uniqueFruktTypes);
    }
}
