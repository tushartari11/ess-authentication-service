package de.vitapublic.essAuthenticationService.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vitapublic.essAuthenticationService.common.ErrorResponse;
import de.vitapublic.essAuthenticationService.security.exceptions.AuthMethodNotSupportedException;
import de.vitapublic.essAuthenticationService.security.exceptions.JwtExpiredTokenException;
import de.vitapublic.essAuthenticationService.security.exceptions.JwtInvalidContextException;
import de.vitapublic.essAuthenticationService.security.exceptions.JwtInvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("security.authentication.jwtAuthenticationFailureHandler")
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper mapper;

    @Autowired
    public JwtAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse.headers(response);

        if (e instanceof BadCredentialsException) {
            mapper.writeValue(response.getWriter(), getErrorResponse("Authentication failed", request));
        } else if (e instanceof JwtExpiredTokenException) {
            mapper.writeValue(response.getWriter(), getErrorResponse("Token has expired", request));
        } else if (e instanceof AuthMethodNotSupportedException) {
            mapper.writeValue(response.getWriter(), getErrorResponse(e.getMessage(), request));
        } else if (e instanceof JwtInvalidTokenException) {
            mapper.writeValue(response.getWriter(), getErrorResponse(e.getMessage(), request));
        } else if (e instanceof JwtInvalidContextException) {
            mapper.writeValue(response.getWriter(), getErrorResponse(e.getMessage(), request));
        } else {
            mapper.writeValue(response.getWriter(), getErrorResponse("Authentication failed", request));
        }
    }

    private ErrorResponse getErrorResponse(String message, HttpServletRequest request) {
        return ErrorResponse.create(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), message, request.getRequestURI());
    }
}
