package de.vitapublic.essAuthenticationService.security.authorization;

import de.vitapublic.essAuthenticationService.security.exceptions.JwtInvalidContextException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashMap;
import java.util.Map;

public class Roles {

    public static final String ROLE_ESS_CLIENT = "ROLE_ESS_CLIENT";
    public static final String ROLE_ESS_USER = "ROLE_ESS_USER";
    public static final String ROLE_ESS_TOURNAMENT_ADMIN = "ROLE_ESS_TOURNAMENT_ADMIN";
    public static final String ROLE_ESS_SUPER_ADMIN = "ROLE_ESS_SUPER_ADMIN";
    public static final String ROLE_ESS_SYSTEM = "ROLE_ESS_SYSTEM"; // Interaction between API-servers, deployment and co.

    public static final SimpleGrantedAuthority AUTHORITY_ESS_CLIENT = new SimpleGrantedAuthority(ROLE_ESS_CLIENT);

    public static final SimpleGrantedAuthority AUTHORITY_ESS_USER = new SimpleGrantedAuthority(ROLE_ESS_USER);
    public static final SimpleGrantedAuthority AUTHORITY_ESS_TOURNAMENT_ADMIN = new SimpleGrantedAuthority(ROLE_ESS_TOURNAMENT_ADMIN);
    public static final SimpleGrantedAuthority AUTHORITY_ESS_SUPER_ADMIN = new SimpleGrantedAuthority(ROLE_ESS_SUPER_ADMIN);
    public static final SimpleGrantedAuthority AUTHORITY_ESS_SYSTEM = new SimpleGrantedAuthority(ROLE_ESS_SYSTEM);

    public static final Map<String, GrantedAuthority> userIdentityRoleMapping = new HashMap<>();

    static {
        userIdentityRoleMapping.put("Application/ess-user", AUTHORITY_ESS_USER);
        userIdentityRoleMapping.put("Application/ess-tournament-admin", AUTHORITY_ESS_TOURNAMENT_ADMIN);
        userIdentityRoleMapping.put("Application/ess-super-admin", AUTHORITY_ESS_SUPER_ADMIN);
    }

    public static GrantedAuthority getUserAuthority(String[] roleClaims, Map<String, Object> audience) {
        GrantedAuthority authority;
        for (String roleClaim : roleClaims) {
            if (userIdentityRoleMapping.containsKey(roleClaim)) {
                authority = userIdentityRoleMapping.get(roleClaim);
                if (AuthUtil.isAdminAudience(audience) && AuthUtil.isAdmin(authority)) {
                    return authority;
                } else if (AuthUtil.isClientAudience(audience) && AuthUtil.isUser(authority)) {
                    return authority;
                }
            }
        }
        // Combination of role and audience didn't match
        throw new JwtInvalidContextException("Invalid Context");
    }

    public static GrantedAuthority getClientAuthority(Map<String, Object> audience) {
        if (AuthUtil.isAdminAudience(audience)) {
            throw new JwtInvalidContextException("Invalid context");
        } else if (AuthUtil.isSystemAudience(audience)) {
            return Roles.AUTHORITY_ESS_SYSTEM;
        } else {
            return Roles.AUTHORITY_ESS_CLIENT;
        }
    }
}
