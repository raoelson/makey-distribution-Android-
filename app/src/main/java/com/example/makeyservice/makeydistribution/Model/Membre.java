package com.example.makeyservice.makeydistribution.Model;

/**
 * Created by RAYA on 29/08/2016.
 */
public class Membre {
    private Integer id;
    private String nom;
    private String prenom;
    private String staff_type;
    private String email;
    private String telephone_perso;
    private String telephone_pro;
    private String mail_pro;
    private String id_staff;
    private String password;
    private String image;
    private String statut;
    private Integer online,timeStart;
    private String date_creation;
    private boolean selected = false;
    private Integer theme;

    public Membre() {
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public Integer getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Integer timeStart) {
        this.timeStart = timeStart;
    }

    public Membre(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(String date_creation) {
        this.date_creation = date_creation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
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

    public String getStaff_type() {
        return staff_type;
    }

    public void setStaff_type(String staff_type) {
        this.staff_type = staff_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelephone_perso() {
        return telephone_perso;
    }

    public void setTelephone_perso(String telephone_perso) {
        this.telephone_perso = telephone_perso;
    }

    public String getTelephone_pro() {
        return telephone_pro;
    }

    public void setTelephone_pro(String telephone_pro) {
        this.telephone_pro = telephone_pro;
    }

    public String getMail_pro() {
        return mail_pro;
    }

    public void setMail_pro(String mail_pro) {
        this.mail_pro = mail_pro;
    }

    public Integer getTheme() {
        return theme;
    }

    public void setTheme(Integer theme) {
        this.theme = theme;
    }

    public String getId_staff() {
        return id_staff;
    }

    public void setId_staff(String id_staff) {
        this.id_staff = id_staff;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
