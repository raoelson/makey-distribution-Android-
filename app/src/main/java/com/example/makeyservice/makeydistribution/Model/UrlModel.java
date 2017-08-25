package com.example.makeyservice.makeydistribution.Model;

/**
 * Created by makeyservice on 16/05/2017.
 */

public class UrlModel {

    private String url;
    private String message;
    private String error;

    public UrlModel() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
