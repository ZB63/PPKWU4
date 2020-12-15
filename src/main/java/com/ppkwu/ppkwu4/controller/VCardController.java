package com.ppkwu.ppkwu4.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VCardController {

    @GetMapping("test")
    public String test(@RequestParam(value = "name") String name) {
        String url = "https://panoramafirm.pl/szukaj?k=" + name;

        return url;
    }

}
