package de.vitapublic.essAuthenticationService.security.model;

public class User {

    private String identity;
    private String identityName;

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    @Override
    public String toString() {
        return "User{" +
                "identity='" + identity + '\'' +
                ", identityName='" + identityName + '\'' +
                '}';
    }
}
