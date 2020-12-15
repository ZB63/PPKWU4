package com.ppkwu.ppkwu4.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class VCardController {

    @GetMapping("test/{name}")
        public String test(@PathVariable String name) throws IOException {
        String url = "https://panoramafirm.pl/szukaj?k=" + name;

        Document doc = Jsoup
                .connect(url)
                .get();

        Elements table = doc.select("script");

        return url;
    }

}
