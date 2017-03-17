package de.vitapublic.essAuthenticationService.common;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;

public class ErrorResponse {

    private long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    private ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public static ErrorResponse create(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path);
    }

    public static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlAllowCredentials(true);
        headers.setAccessControlAllowHeaders(Collections.singletonList("Authorization"));
        headers.setAccessControlAllowMethods(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE));
        headers.setAccessControlAllowOrigin("*");
        return headers;
    }

    public static void headers(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,PATCH,DELETE");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Authorization");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
