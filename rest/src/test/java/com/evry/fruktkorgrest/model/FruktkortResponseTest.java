package com.evry.fruktkorgrest.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.sql.Timestamp;

public class FruktkortResponseTest {

    @Test
    public void createFruktkorgResponse() {
        FruktkorgResponse fruktkorgResponse = new FruktkorgResponse();
        fruktkorgResponse.setLastChanged(Timestamp.valueOf("2018-08-14 08:00:00"));
        Assertions.assertEquals("14 augusti 08:00", fruktkorgResponse.getLastChanged());
    }
}
