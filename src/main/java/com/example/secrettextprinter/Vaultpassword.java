package com.example.secrettextprinter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("vaultpassword")
public class Vaultpassword {

    private String password;
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasssword() {
        return password;
    }



}
