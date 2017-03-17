package de.vitapublic.essAuthenticationService.security.authentication;

import de.vitapublic.essAuthenticationService.security.authorization.AudienceProvider;
import de.vitapublic.essAuthenticationService.security.authorization.Roles;
import de.vitapublic.essAuthenticationService.security.config.JwtSettings;
import de.vitapublic.essAuthenticationService.security.exceptions.JwtInvalidTokenException;
import de.vitapublic.essAuthenticationService.security.model.RawAccessJwtToken;
import de.vitapublic.essAuthenticationService.security.model.User;
import de.vitapublic.essAuthenticationService.security.model.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("security.authentication.jwtAuthenticationProvider")
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtSettings jwtSettings;
    private AudienceProvider audienceProvider;

    @Autowired
    public JwtAuthenticationProvider(JwtSettings jwtSettings, AudienceProvider audienceProvider) {
        this.jwtSettings = jwtSettings;
        this.audienceProvider = audienceProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();

        Jws<Claims> jwsClaims;
        try {
            jwsClaims = rawAccessToken.parseClaims(jwtSettings.getSigningKey());
        } catch (Exception e) {
            throw new JwtInvalidTokenException(rawAccessToken, "Invalid Jwt token", e);
        }

        UserContext context;
        if (isClientToken(jwsClaims)) {
            context = createClientContext(jwsClaims);
        } else {
            context = createUserContext(jwsClaims);
        }
        return new JwtAuthenticationToken(context, Collections.singleton(context.getAuthority()));
    }

    private UserContext createClientContext(Jws<Claims> jwsClaims) {
        Map<String, Object> audience = getClient(jwsClaims);
        return UserContext.create(audience, Roles.getClientAuthority(audience));
    }

    private UserContext createUserContext(Jws<Claims> jwsClaims) {
        Map<String, Object> audience = getClient(jwsClaims);
        User user = getUser(jwsClaims);
        GrantedAuthority authority = getUserAuthority(jwsClaims, audience);
        return UserContext.create(user, audience, authority);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    private Boolean isClientToken(Jws<Claims> jwsClaims) {
        return null == jwsClaims.getBody().getSubject();
    }

    private User getUser(Jws<Claims> jwsClaims) {
        String userIdentity = jwsClaims.getBody().getSubject();
        String userName = jwsClaims.getBody().get("username", String.class);
        if (null == userName) {
            userName = userIdentity.substring(0, userIdentity.lastIndexOf('@'));
        }

        User user = new User();
        user.setIdentity(userIdentity);
        user.setIdentityName(userName);
        return user;
    }

    private Map<String, Object> getClient(Jws<Claims> jwsClaims) {
        List<String> clients = jwsClaims.getBody().get("aud", List.class);
        if (null == clients || clients.size() != 1) {
            throw new JwtInvalidTokenException("Invalid audience");
        }
        Map<String, Object> audience = audienceProvider.getAudience(clients.get(0));
        if (null == audience) {
            throw new JwtInvalidTokenException("Invalid audience");
        }
        return audience;
    }

    private GrantedAuthority getUserAuthority(Jws<Claims> jwsClaims, Map<String, Object> client) {
        String roleString = jwsClaims.getBody().get(jwtSettings.getRoleClaimName(), String.class);
        String[] roles = new String[0];
        if (null != roleString && roleString.length() > 0) {
            roles = roleString.split(",");
        }
        return Roles.getUserAuthority(roles, client);
    }
}
