package de.vitapublic;


import de.vitapublic.essAuthenticationService.model.Claim;
import de.vitapublic.essAuthenticationService.model.User;
import de.vitapublic.essAuthenticationService.model.UserProperties;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/integrationtest.properties")
public class UserServiceImplTest {

//    @Autowired
//    RestTemplate restTemplate;

    @Value("${wso2is.host-address}")
    private String hostAddress;

    @Value("${wso2is.host-scheme}")
    private String hostScheme;

    @Value("${wso2is.host-port}")
    private String hostPort;

    @Value("${wso2is.register.user.uri}")
    private String registerUserUri;

    @Value("${ess.services.security.key-store}")
    private String keyStoreFile;

    @Value("${ess.services.security.key-store-password}")
    private String keyStorePassword;


    @Test
    public void registerUser() {

        String restUrl = getRegisterUserUri();
        User usertoRegister = buildUserObject();
        User registeredUser = null;
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("Authorization", "Bearer eyJ4NXQiOiJOakUyWWpCbVpqRTFObUZrWkRSaFltVXpNelprTVdWak16WXlNRE0xTldZMllqVTRZak14TnciLCJraWQiOiJkMGVjNTE0YTMyYjZmODhjMGFiZDEyYTI4NDA2OTliZGQzZGViYTlkIiwiYWxnIjoiUlMyNTYifQ.eyJhdF9oYXNoIjoiX0F0RE5LeEpiN1FPelV5ajA4YUxHdyIsImFjciI6InVybjptYWNlOmluY29tbW9uOmlhcDpzaWx2ZXIiLCJzdWIiOiJ0ZXN0c3VwZXJhZG1pbkBjYXJib24uc3VwZXIiLCJhdWQiOlsiUVhCajY5cUI2Sk9KVlZpZEJFU3ZPejQ4ZVA4YSJdLCJyb2xlIjoiSW50ZXJuYWxcL2V2ZXJ5b25lLEFwcGxpY2F0aW9uXC9lc3Mtc3VwZXItYWRtaW4iLCJhenAiOiJRWEJqNjlxQjZKT0pWVmlkQkVTdk96NDhlUDhhIiwic2NvcGUiOiJvcGVuaWQiLCJpc3MiOiJodHRwczpcL1wvbmdsLXRlc3Qudml0YXB1YmxpYy5kZTo0NDNcL29hdXRoMlwvdG9rZW4iLCJleHAiOjE0ODk0ODYzOTYsImlhdCI6MTQ4OTQ4Mjc5Nn0.PNwBcYW0SpBzam5XVF70Tcxt6HJIQhC6h-GJQri5GxrHIvlSo0DXAqYYxknW2zYXsFHQ42RzsGVcvxYzzyXo-FP69Mrxf99t28MbZd1Zk25NlOpVdPjjc_qMwHTl-BA762wv0XvIYrCdU8wkNz9v151llKTL3dDdGQmaO9G-Wn2yfUiIRF-Wh6tE9TRg4G9jnXF0UtY2OQ3DJchdthootXhd8-PL9nTGwbEQPMDxQbzbqs5ZMilBEobGVAMXBjr0dgRkrC0945_5Idgnkx3AdMH4FPsiw4PobnVKJFyzxjPszS4lZB18XEcoBqlhNB5eyqP-d3u__rZOSBJViwraLA");

            HttpEntity<User> entity = new HttpEntity<User>(usertoRegister, headers);

            RestTemplate restTemplate = new RestTemplate();
//            registeredUser = restTemplate.postForObject(restUrl, usertoRegister, User.class, buildUserObject());
            ResponseEntity<User> result = restTemplate.exchange(restUrl, HttpMethod.POST, entity, User.class);
            System.out.println("result : "+result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.out.println("registered user :" + registeredUser);
    }


    private User buildUserObject() {

        User user = new User();

        user.setUsername("isura");
        user.setRealm("ENGINEERING");
        user.setPassword("P@ssw0rd2017");


        List<Claim> claims = new ArrayList<>();
        Claim claim = new Claim();
        claim.setUri("http://wso2.org/claims/givenname");
        claim.setValue("Max");
        claims.add(claim);

        claim = new Claim();
        claim.setUri("http://wso2.org/claims/emailaddres");
        claim.setValue("max.mustermann@gmail.com");
        claims.add(claim);

        claim = new Claim();
        claim.setUri("http://wso2.org/claims/lastname");
        claim.setValue("Mustermann");
        claims.add(claim);

        claim = new Claim();
        claim.setUri("http://wso2.org/claims/mobile");
        claim.setValue("491768881212");
        claims.add(claim);

        user.setClaims(claims);
        user.setProperties(new ArrayList<UserProperties>());

        return user;
    }

    private String getEncryptedClientKey(String clientKey, String clientSecret) {
        String plainCreds = clientKey + ":" + clientSecret;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        return base64Creds;
    }

    private HttpHeaders getHttpHeaders() {
        String clientkey = "rKbPz5ijcE_h91ruroD0HlHCdaYa";
        String clientsecret = "miE9gkXo0LLWNLLZRTTdhLkk3eEa";
        String encryptedClientKey = getEncryptedClientKey(clientkey, clientsecret);
        String authHeader = "Basic " + new String(encryptedClientKey);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", authHeader);
        return headers;
    }

    private String getRegisterUserUri() {
        String restUrl = hostScheme + hostAddress + ":" + hostPort + registerUserUri;
        return restUrl;
    }

}
