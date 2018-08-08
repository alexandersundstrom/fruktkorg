package com.evry.fruktkorg;

import com.evry.fruktkorg.persistence.FruktkorgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FruktController {

    @Autowired
    private FruktkorgService fruktkorgService;

//    @Autowired(required=true)
//    @Qualifier(value = "fruktkorgService")
//    public FruktController(FruktkorgService fruktkorgService) {
//        this.fruktkorgService = fruktkorgService;
//    }

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "pong";
    }

    @GetMapping("/count")
    @ResponseBody
    public Integer count() {
        return fruktkorgService.listFruktkorg().size();
    }
}
