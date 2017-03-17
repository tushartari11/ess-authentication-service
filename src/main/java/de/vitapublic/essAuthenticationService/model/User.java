package de.vitapublic.essAuthenticationService.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {

    private String username;
    private String realm;
    private String password;
    @JsonIgnore
    private String passwordConfirm;
    @JsonIgnore
    private List<Claim> claims;
    @JsonIgnore
    private List<UserProperties> properties;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public List<Claim> getClaims() {
        return claims;
    }

    public void setClaims(List<Claim> claims) {
        this.claims = claims;
    }

    public List<UserProperties> getProperties() {
        return properties;
    }

    public void setProperties(List<UserProperties> properties) {
        this.properties = properties;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", realm=" + realm + ", password=" + password + ", passwordConfirm="
                + passwordConfirm + ", claims=" + claims + ", properties=" + properties + "]";
    }
}

