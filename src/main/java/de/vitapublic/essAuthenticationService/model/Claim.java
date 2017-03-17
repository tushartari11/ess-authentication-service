package de.vitapublic.essAuthenticationService.model;


public class Claim {

    private String uri;
    private String value;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
