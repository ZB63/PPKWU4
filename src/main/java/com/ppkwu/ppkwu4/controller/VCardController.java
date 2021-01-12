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
import sun.tools.jconsole.JConsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class VCardController {

    @GetMapping("search/{phraseToSearch}")
    public List<VCardDTO> search(@PathVariable String phraseToSearch) throws IOException {
        String url = "https://panoramafirm.pl/szukaj?k=" + phraseToSearch;

        Document doc = Jsoup
                .connect(url)
                .get();

        Elements elements = doc.select("script");

        List<VCardDTO> cardsList = new ArrayList<>();

        for(int i=0;i<elements.size() - 2;i++) {

            if(elements.get(i).attr("type").equals("application/ld+json")) {
                String json = elements.get(i).data();
                //System.out.println(json);
                JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                if(jsonObject.has("name")) {
                    cardsList.add(jsonToVCardDTO(jsonObject));
                }
            }
        }

        return cardsList;
    }

    @GetMapping("vcard/{phraseToSearch}/{name}")
    public List<VCardDTO> generateVCard(@PathVariable String phraseToSearch, @PathVariable String name) throws IOException {
        String url = "https://panoramafirm.pl/szukaj?k=" + phraseToSearch;

        Document doc = Jsoup
                .connect(url)
                .get();

        Elements elements = doc.select("script");

        for(int i=0;i<elements.size() - 2;i++) {

            if(elements.get(i).attr("type").equals("application/ld+json")) {
                String json = elements.get(i).data();
                JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                if(jsonObject.has("name") && jsonObject.get("name").getAsString().equals(name)) {
                    // here build and send vcard
                    System.out.println("Found " + name);
                }
            }
        }

        return null;
    }

    private VCardDTO jsonToVCardDTO(JsonObject jsonObject) {
        VCardDTO vCardDTO = new VCardDTO();
        if(jsonObject.has("name")) {
            vCardDTO.setFormattedName(jsonObject.get("name").getAsString());
        }
        if(jsonObject.has("email")) {
            vCardDTO.setEmail(jsonObject.get("email").getAsString());
        }
        if(jsonObject.has("telephone")) {
            vCardDTO.setTelephoneNumber(jsonObject.get("telephone").getAsString());
        }
        if(jsonObject.has("sameAs")) {
            vCardDTO.setUrl(jsonObject.get("sameAs").getAsString());
        }
        return vCardDTO;
    }

}
