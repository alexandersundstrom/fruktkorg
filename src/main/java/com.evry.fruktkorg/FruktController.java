package com.evry.fruktkorg;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FruktController {

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String ping() {
        return "pong";
    }
}
