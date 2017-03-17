package de.vitapublic.essAuthenticationService.security.authentication.extractor;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component("security.authentication.extractor.jwtHeaderTokenExtractor")
public class JwtHeaderTokenExtractor implements TokenExtractor {
    public static String HEADER_PREFIX = "Bearer ";

    @Override
    public String extract(String header) {
        if (null == header) {
            throw new AuthenticationServiceException("Invalid authorization header.");
        }
        if (header.length() < HEADER_PREFIX.length()) {
            throw new AuthenticationServiceException("Invalid authorization header.");
        }
        if (!header.startsWith(HEADER_PREFIX)) {
            throw new AuthenticationServiceException("Invalid authorization header.");
        }
        return header.substring(HEADER_PREFIX.length(), header.length());
    }
}