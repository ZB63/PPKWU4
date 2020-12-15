package com.ppkwu.ppkwu4.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VCardController {

    @GetMapping("test/{name}")
        public String test(@PathVariable String name) {
        String url = "https://panoramafirm.pl/szukaj?k=" + name;



        return url;
    }

}
