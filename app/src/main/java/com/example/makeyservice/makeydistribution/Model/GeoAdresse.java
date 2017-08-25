package com.example.makeyservice.makeydistribution.Model;

/**
 * Created by makeyservice on 19/06/2017.
 */

public class GeoAdresse {
    Double score;
    String street;
    Integer housenumber;
    Integer postcode;
    String city;
    Integer id;
    String chemin;
    boolean isSelected;

    public GeoAdresse() {
    }

    public String getChemin() {
        return chemin;
    }

    public void setChemin(String chemin) {
        this.chemin = chemin;
    }

    public Double getScore() {
        return score;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getHousenumber() {
        return housenumber;
    }

    public void setHousenumber(Integer housenumber) {
        this.housenumber = housenumber;
    }

    public Integer getPostcode() {
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
