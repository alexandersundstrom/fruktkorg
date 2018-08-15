package com.evry.fruktkorgrest.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.sql.Timestamp;

public class FruktkortResponseTest {

    @Test
    public void createFruktkorgResponse() {
        FruktkorgResponse fruktkorgResponse = new FruktkorgResponse();
        fruktkorgResponse.setLastChangedFromTimeStamp(Timestamp.valueOf("2018-08-14 08:00:00"));
        Assertions.assertEquals("14 augusti 08:00", fruktkorgResponse.getLastChanged(), "Timestamp provided, expected last change to be converted to String");
    }

    @Test
    public void createFruktkorgResponseWithLastChangeNull() {
        FruktkorgResponse fruktkorgResponse = new FruktkorgResponse();
        fruktkorgResponse.setLastChanged(null);
        Assertions.assertNull(fruktkorgResponse.getLastChanged(), "When no timestamp is provided, last changed should be null");
    }
}
