package com.evry.fruktkorgrest.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

class FruktkortResponseTest {

    @Test
    void createFruktkorgResponse() {
        FruktkorgResponse fruktkorgResponse = new FruktkorgResponse();
        fruktkorgResponse.setLastChangedFromInstant(Instant.parse("2018-08-14T08:00:00.00Z"));
        Assertions.assertEquals("14 augusti 10:00", fruktkorgResponse.getLastChanged(), "Instant provided, expected last change to be converted to String");
    }

    @Test
    void createFruktkorgResponseWithLastChangeNull() {
        FruktkorgResponse fruktkorgResponse = new FruktkorgResponse();
        fruktkorgResponse.setLastChanged(null);
        Assertions.assertNull(fruktkorgResponse.getLastChanged(), "When no instant is provided, last changed should be null");
    }
}
