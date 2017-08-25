package com.example.makeyservice.makeydistribution.Model;

/**
 * Created by makeyservice on 02/06/2017.
 */

public class Adresse {
    private String cpVille;
    private String adresse;
    private String nomPrenom;
    private String complementAdresse;
    private String cp;
    private String ville;
    private String infoPortage;

    public Adresse() {
    }

    public String getCpVille() {
        return cpVille;
    }

    public void setCpVille(String cpVille) {
        this.cpVille = cpVille;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNomPrenom() {
        return nomPrenom;
    }

    public void setNomPrenom(String nomPrenom) {
        this.nomPrenom = nomPrenom;
    }

    public String getComplementAdresse() {
        return complementAdresse;
    }

    public void setComplementAdresse(String complementAdresse) {
        this.complementAdresse = complementAdresse;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getInfoPortage() {
        return infoPortage;
    }

    public void setInfoPortage(String infoPortage) {
        this.infoPortage = infoPortage;
    }
}
