package de.vitapublic.essAuthenticationService.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JwtInvalidContextException extends AuthenticationException {

    public JwtInvalidContextException(String msg, Throwable t) {
        super(msg, t);
    }

    public JwtInvalidContextException(String msg) {
        super(msg);
    }
}
