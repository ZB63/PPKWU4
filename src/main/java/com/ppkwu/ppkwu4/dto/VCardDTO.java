package com.ppkwu.ppkwu4.dto;

import ezvcard.VCard;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;

public class VCardDTO {
    private String formattedName;
    private String telephoneNumber;
    private String email;
    private String url;

    public String getFormattedName() {
        return formattedName;
    }

    public void setFormattedName(String formattedName) {
        this.formattedName = formattedName;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public VCard buildVcard() {
        VCard vcard = new VCard();
        vcard.setFormattedName(formattedName);
        vcard.addTelephoneNumber(telephoneNumber, TelephoneType.WORK);
        vcard.addEmail(email, EmailType.WORK);
        vcard.addUrl(url);
        return vcard;
    }

}
