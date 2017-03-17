package de.vitapublic.essAuthenticationService.security.exceptions;

import de.vitapublic.essAuthenticationService.security.model.JwtToken;
import org.springframework.security.core.AuthenticationException;

public class JwtInvalidTokenException extends AuthenticationException {

    private static final long serialVersionUID = 5519731225934810022L;
    private JwtToken token;

    public JwtInvalidTokenException(String msg) {
        super(msg);
    }

    public JwtInvalidTokenException(JwtToken token, String msg, Throwable t) {
        super(msg, t);
        this.token = token;
    }

    public String token() {
        return this.token.getToken();
    }
}
