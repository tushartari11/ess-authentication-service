package de.vitapublic.essAuthenticationService.service;


import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class AuthenticationService {

    public Map<String, String> authenticate(String authCredentials) {

        if (null == authCredentials)
            return null;
        // header value format will be "Basic encodedstring" for Basic
        // authentication. Example "Basic YWRtaW46YWRtaW4="
        final String encodedClientKey = authCredentials.replaceFirst("Basic"
                + " ", "");
        String clientToken = null;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedClientKey);
            clientToken = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final StringTokenizer tokenizer = new StringTokenizer(
                clientToken, ":");
        final String clientkey = tokenizer.nextToken();
        final String clientsecret = tokenizer.nextToken();

        Map<String, String> clientCredentials = new HashMap<>();
        clientCredentials.put("clientkey", clientkey);
        clientCredentials.put("clientsecret", clientsecret);
        return clientCredentials;
    }
}
