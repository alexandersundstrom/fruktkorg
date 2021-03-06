package com.evry.fruktkorgrest.servlet.controller.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class FruktkorgDTOTest {

    @Test
    void createFruktkorgDTO() {
        FruktkorgDTO fruktkorgDTO = new FruktkorgDTO();
        fruktkorgDTO.setLastChangedFromInstant(Instant.parse("2018-08-14T08:00:00.00Z"));
        Assertions.assertEquals("14 augusti 10:00", fruktkorgDTO.getLastChanged(), "Instant provided, expected last change to be converted to String");
    }

    @Test
    void createFruktkorgDTOWithLastChangeNull() {
        FruktkorgDTO fruktkorgDTO = new FruktkorgDTO();
        fruktkorgDTO.setLastChanged(null);
        Assertions.assertNull(fruktkorgDTO.getLastChanged(), "When no instant is provided, last changed should be null");
    }
}
