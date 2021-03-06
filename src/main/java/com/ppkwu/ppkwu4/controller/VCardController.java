package com.ppkwu.ppkwu4.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ppkwu.ppkwu4.dto.VCardDTO;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public ResponseEntity generateVCard(@PathVariable String phraseToSearch, @PathVariable String name) throws IOException {
        String url = "https://panoramafirm.pl/szukaj?k=" + phraseToSearch;

        Document doc = Jsoup
                .connect(url)
                .get();

        Elements elements = doc.select("script");

        boolean fileCreated = false;

        for(int i=0;i<elements.size() - 2;i++) {
            if(elements.get(i).attr("type").equals("application/ld+json")) {
                String json = elements.get(i).data();
                JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                if(jsonObject.has("name") && jsonObject.get("name").getAsString().equals(name)) {
                    // here build and send vcard
                    VCard vCard = jsonToVCard(jsonObject);

                    File file = new File("VCard - " + name + ".vcf");
                    Ezvcard.write(vCard).version(VCardVersion.V3_0).go(file);
                    fileCreated = true;
                    break;
                }
            }
        }

        if(fileCreated) {
            Path path = Paths.get("VCard - " + name + ".vcf");
            Resource resource = null;
            try {
                resource = new UrlResource(path.toUri());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    private VCard jsonToVCard(JsonObject jsonObject) {
        VCard vcard = new VCard();

        if(jsonObject.has("name")) {
            vcard.setFormattedName(jsonObject.get("name").getAsString());
        }
        if(jsonObject.has("email")) {
            vcard.addEmail(jsonObject.get("email").getAsString(), EmailType.WORK);
        }
        if(jsonObject.has("telephone")) {
            vcard.addTelephoneNumber(jsonObject.get("telephone").getAsString(), TelephoneType.WORK);
        }
        if(jsonObject.has("sameAs")) {
            vcard.addUrl(jsonObject.get("sameAs").getAsString());
        }

        return vcard;
    }

}
