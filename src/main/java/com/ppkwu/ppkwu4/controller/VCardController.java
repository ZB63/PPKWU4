package com.ppkwu.ppkwu4.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ppkwu.ppkwu4.dto.VCardDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class VCardController {

    @GetMapping("test/{name}")
        public String test(@PathVariable String name) throws IOException {
        String url = "https://panoramafirm.pl/szukaj?k=" + name;

        Document doc = Jsoup
                .connect(url)
                .get();

        Elements elements = doc.select("script");

        List<VCardDTO> cardsList = new ArrayList<>();

        for(int i=0;i<elements.size() - 1;i++) {

            if(elements.get(i).attr("type").equals("application/ld+json")) {
                String json = elements.get(i).data();
                //System.out.println(json);
                JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                cardsList.add(jsonToVCardDTO(jsonObject));
            }
        }

        return "test";
    }

    private VCardDTO jsonToVCardDTO(JsonObject jsonObject) {
        VCardDTO vCardDTO = new VCardDTO();
        vCardDTO.setFormattedName(jsonObject.get("name").getAsString());
        vCardDTO.setEmail(jsonObject.get("email").getAsString());
        vCardDTO.setTelephoneNumber(jsonObject.get("telephone").getAsString());
        vCardDTO.setUrl(jsonObject.get("sameAs").getAsString());
        return vCardDTO;
    }

}
