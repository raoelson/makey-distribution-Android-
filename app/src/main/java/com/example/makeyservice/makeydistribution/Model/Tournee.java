package com.example.makeyservice.makeydistribution.Model;

import android.support.annotation.NonNull;

/**
 * Created by makeyservice on 08/06/2017.
 */

public class Tournee implements Comparable<Tournee>{
    private Integer tourn_id;
    private String tourn_lib;
    private Integer prod_id;
    private String prod_lib;
    private Integer vis_id;
    private Integer vis_quant;
    private String vis_comment;
    private String v_comment_val;
    private Integer pl_id;

    private String p_libel;
    private Integer stop_id;
    private Integer ordre_stop;
    private Integer depot_id;
    private String depot_libelle;

    private String depot_city;
    private String depot_country;
    private String depot_street;
    private String depot_cp;
    private String city;


    private Integer cp;
    private String street;
    private String nom;
    private String type_visite;
    private String type_code;

    private String ptl_lib;
    private Integer ptl_id;
    private String ptl_comment;
    private String vcptrs_id;
    private String vcptrs_libelle;

    private Integer cptrs_id;
    private String cptrs_libelle;

    public Tournee() {
    }

    public Integer getTourn_id() {
        return tourn_id;
    }

    public void setTourn_id(Integer tourn_id) {
        this.tourn_id = tourn_id;
    }

    public String getTourn_lib() {
        return tourn_lib;
    }

    public void setTourn_lib(String tourn_lib) {
        this.tourn_lib = tourn_lib;
    }

    public Integer getProd_id() {
        return prod_id;
    }

    public void setProd_id(Integer prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_lib() {
        return prod_lib;
    }

    public void setProd_lib(String prod_lib) {
        this.prod_lib = prod_lib;
    }

    public Integer getVis_id() {
        return vis_id;
    }

    public void setVis_id(Integer vis_id) {
        this.vis_id = vis_id;
    }

    public Integer getVis_quant() {
        return vis_quant;
    }

    public void setVis_quant(Integer vis_quant) {
        this.vis_quant = vis_quant;
    }

    public String getVis_comment() {
        return vis_comment;
    }

    public void setVis_comment(String vis_comment) {
        this.vis_comment = vis_comment;
    }

    public String getV_comment_val() {
        return v_comment_val;
    }

    public void setV_comment_val(String v_comment_val) {
        this.v_comment_val = v_comment_val;
    }

    public Integer getPl_id() {
        return pl_id;
    }

    public void setPl_id(Integer pl_id) {
        this.pl_id = pl_id;
    }

    public String getP_libel() {
        return p_libel;
    }

    public void setP_libel(String p_libel) {
        this.p_libel = p_libel;
    }

    public Integer getStop_id() {
        return stop_id;
    }

    public void setStop_id(Integer stop_id) {
        this.stop_id = stop_id;
    }

    public Integer getOrdre_stop() {
        return ordre_stop;
    }

    public void setOrdre_stop(Integer ordre_stop) {
        this.ordre_stop = ordre_stop;
    }

    public Integer getDepot_id() {
        return depot_id;
    }

    public void setDepot_id(Integer depot_id) {
        this.depot_id = depot_id;
    }

    public String getDepot_libelle() {
        return depot_libelle;
    }

    public void setDepot_libelle(String depot_libelle) {
        this.depot_libelle = depot_libelle;
    }

    public String getDepot_city() {
        return depot_city;
    }

    public void setDepot_city(String depot_city) {
        this.depot_city = depot_city;
    }

    public String getDepot_country() {
        return depot_country;
    }

    public void setDepot_country(String depot_country) {
        this.depot_country = depot_country;
    }

    public String getDepot_street() {
        return depot_street;
    }

    public void setDepot_street(String depot_street) {
        this.depot_street = depot_street;
    }

    public String getDepot_cp() {
        return depot_cp;
    }

    public void setDepot_cp(String depot_cp) {
        this.depot_cp = depot_cp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getCp() {
        return cp;
    }

    public void setCp(Integer cp) {
        this.cp = cp;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType_visite() {
        return type_visite;
    }

    public void setType_visite(String type_visite) {
        this.type_visite = type_visite;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getPtl_lib() {
        return ptl_lib;
    }

    public void setPtl_lib(String ptl_lib) {
        this.ptl_lib = ptl_lib;
    }

    public Integer getPtl_id() {
        return ptl_id;
    }

    public void setPtl_id(Integer ptl_id) {
        this.ptl_id = ptl_id;
    }

    public String getPtl_comment() {
        return ptl_comment;
    }

    public void setPtl_comment(String ptl_comment) {
        this.ptl_comment = ptl_comment;
    }

    public String getVcptrs_id() {
        return vcptrs_id;
    }

    public void setVcptrs_id(String vcptrs_id) {
        this.vcptrs_id = vcptrs_id;
    }

    public String getVcptrs_libelle() {
        return vcptrs_libelle;
    }

    public void setVcptrs_libelle(String vcptrs_libelle) {
        this.vcptrs_libelle = vcptrs_libelle;
    }

    public Integer getCptrs_id() {
        return cptrs_id;
    }

    public void setCptrs_id(Integer cptrs_id) {
        this.cptrs_id = cptrs_id;
    }

    public String getCptrs_libelle() {
        return cptrs_libelle;
    }

    public void setCptrs_libelle(String cptrs_libelle) {
        this.cptrs_libelle = cptrs_libelle;
    }



    @Override
    public int compareTo(@NonNull Tournee o) {
        int comparestop=((Tournee) o).getOrdre_stop();
        /* For Ascending order*/
        return this.ordre_stop-comparestop;
    }
}
