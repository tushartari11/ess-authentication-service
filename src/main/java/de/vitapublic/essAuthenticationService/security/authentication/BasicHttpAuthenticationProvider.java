package de.vitapublic.essAuthenticationService.security.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component("security.authentication.basicHttpAuthenticationProvider")
public class BasicHttpAuthenticationProvider implements AuthenticationProvider {

    @Value("${security.user.name:developer}")
    String username;

    @Value("${security.user.password:test1234!}")
    String password;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!username.equals(authentication.getPrincipal())
                || !password.equals(authentication.getCredentials())) {
            throw new BadCredentialsException("Username and password error");
        }
        return new UsernamePasswordAuthenticationToken(username, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
