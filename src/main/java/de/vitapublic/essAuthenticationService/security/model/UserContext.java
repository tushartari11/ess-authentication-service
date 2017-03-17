package de.vitapublic.essAuthenticationService.security.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Map;

public class UserContext {

    public enum IdentityTypes {
        USER,
        CLIENT,
    }

    private final IdentityTypes identityType;
    private final User user;
    private final Map<String, Object> audience;
    private final GrantedAuthority authority;

    private UserContext(User user, Map<String, Object> audience, GrantedAuthority authority) {
        this.identityType = IdentityTypes.USER;
        this.user = user;
        this.audience = audience;
        this.authority = authority;
    }

    private UserContext(Map<String, Object> audience, GrantedAuthority authority) {
        this.identityType = IdentityTypes.CLIENT;
        this.user = null;
        this.audience = audience;
        this.authority = authority;
    }

    public static UserContext create(User user, Map<String, Object> audience, GrantedAuthority authority) {
        return new UserContext(user, audience, authority);
    }

    public static UserContext create(Map<String, Object> client, GrantedAuthority authority) {
        return new UserContext(client, authority);
    }

    public IdentityTypes getIdentityType() {
        return identityType;
    }

    public User getUser() {
        return user;
    }

    public Map<String, Object> getAudience() {
        return audience;
    }

    public GrantedAuthority getAuthority() {
        return authority;
    }

    public Long getAudienceOrganizerId() {
        return (Long) audience.get("id");
    }
}
