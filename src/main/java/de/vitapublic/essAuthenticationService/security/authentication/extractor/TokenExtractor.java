package de.vitapublic.essAuthenticationService.security.authentication.extractor;

public interface TokenExtractor {
    public String extract(String payload);
}
