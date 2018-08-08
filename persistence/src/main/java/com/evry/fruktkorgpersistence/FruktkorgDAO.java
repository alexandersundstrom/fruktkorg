package com.evry.fruktkorgpersistence;


import com.evry.fruktkorgpersistence.model.Fruktkorg;

import java.util.List;

public interface FruktkorgDAO {
    List<Fruktkorg> listFruktkorg();
    List<Fruktkorg> findFruktkorgByFrukt();
}
