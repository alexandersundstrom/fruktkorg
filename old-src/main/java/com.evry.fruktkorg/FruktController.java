package com.evry.fruktkorg;

import com.evry.fruktkorg.persistence.FruktkorgService;
import com.evry.fruktkorg.utils.JS;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FruktController {

    private FruktkorgService fruktkorgService;

    @Autowired
    public void setFruktkorgService(FruktkorgService fruktkorgService) {
        this.fruktkorgService = fruktkorgService;
    }

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

    @GetMapping("/list-fruktkorg")
    @ResponseBody
    public ResponseEntity<JsonNode> listFruktkorg() {
        return JS.message(HttpStatus.OK, fruktkorgService.listFruktkorg());
    }

    @GetMapping("/find-fruktkorg")
    @ResponseBody
    public ResponseEntity<JsonNode> findFruktkorg() {
        return JS.message(HttpStatus.OK, fruktkorgService.findFruktkorgByFrukt());
    }
}
