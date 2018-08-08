package com.evry.fruktkorg;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FruktController {

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "pong";
    }
}
