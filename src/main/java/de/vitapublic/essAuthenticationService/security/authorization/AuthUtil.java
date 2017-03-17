package de.vitapublic.essAuthenticationService.security.authorization;

import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.Objects;

import static de.vitapublic.essAuthenticationService.security.authorization.AudienceProvider.ADMIN_AUDIENCE_ID;
import static de.vitapublic.essAuthenticationService.security.authorization.AudienceProvider.SYSTEM_AUDIENCE_ID;
import static de.vitapublic.essAuthenticationService.security.authorization.Roles.*;

public class AuthUtil {
    public static Boolean isClient(GrantedAuthority authority) {
        return AUTHORITY_ESS_CLIENT.equals(authority);
    }

    public static Boolean isUser(GrantedAuthority authority) {
        return AUTHORITY_ESS_USER.equals(authority);
    }

    public static Boolean isAdmin(GrantedAuthority authority) {
        return AUTHORITY_ESS_TOURNAMENT_ADMIN.equals(authority)
                || AUTHORITY_ESS_SUPER_ADMIN.equals(authority);
    }

    public static Boolean isSystem(GrantedAuthority authority) {
        return AUTHORITY_ESS_SYSTEM.equals(authority);
    }

    public static Boolean isAdminAudience(Map<String, Object> audienceObject) {
        return audienceObject.containsKey("id") && Objects.equals(audienceObject.get("id"), ADMIN_AUDIENCE_ID);
    }

    public static Boolean isSystemAudience(Map<String, Object> audienceObject) {
        return audienceObject.containsKey("id") && Objects.equals(audienceObject.get("id"), SYSTEM_AUDIENCE_ID);
    }

    public static Boolean isClientAudience(Map<String, Object> audienceObject) {
        return audienceObject.containsKey("id") && (Long)audienceObject.get("id") > 0L;
    }
}
