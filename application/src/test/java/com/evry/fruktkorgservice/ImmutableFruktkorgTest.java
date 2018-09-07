package com.evry.fruktkorgservice;

import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ImmutableFruktkorgTest {

    @Test
    void listShouldBeUnmodifiable() {
        List<ImmutableFrukt> fruktList = new ArrayList<>();
        fruktList.add(new ImmutableFrukt(1, "Banan", 5, 1));
        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorg(1, "Test", fruktList, Instant.now());
        Assertions.assertThrows(UnsupportedOperationException.class, () -> immutableFruktkorg.getFruktList().add(new ImmutableFrukt(1, "Banan", 5, 1)), "List should be unmodifiable");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> immutableFruktkorg.getFruktList().clear(), "List should be unmodifiable");
    }

    @Test
    void listShouldBeUnmodifiableWhenInitiatedWithNull() {
        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorg(1, "Test", null, Instant.now());
        Assertions.assertNotNull(immutableFruktkorg.getFruktList(), "List should not be null");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> immutableFruktkorg.getFruktList().add(new ImmutableFrukt(1, "Banan", 5, 1)), "List should be unmodifiable");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> immutableFruktkorg.getFruktList().clear(), "List should be unmodifiable");
    }
}
