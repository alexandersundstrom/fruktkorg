package com.evry.fruktkorgservice.service;


import com.evry.fruktkorgservice.model.ImmutableFruktkorg;

public interface FruktkorgService {
    ImmutableFruktkorg createFruktkorg(ImmutableFruktkorg immutableFruktkorg);
    void deleteFruktkorg(long fruktkorgId);
}
