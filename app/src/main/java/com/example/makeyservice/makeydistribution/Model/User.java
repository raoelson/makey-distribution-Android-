package com.example.makeyservice.makeydistribution.Model;

/**
 * Created by makeyservice on 22/05/2017.
 */

public class User {
    private String nom;
    private String prenom;
    private String email;
    private String username;

    public User() {
    }

    public String getNom() {
        return nom;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
