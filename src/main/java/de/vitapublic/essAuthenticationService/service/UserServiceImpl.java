package de.vitapublic.essAuthenticationService.service;

import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import de.vitapublic.essAuthenticationService.common.MyResponseErrorHandler;
import de.vitapublic.essAuthenticationService.common.RestConfig;
import de.vitapublic.essAuthenticationService.controller.exception.LogicalException;
import de.vitapublic.essAuthenticationService.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    RestConfig restConfig;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public String registerUser(User user) throws LogicalException {

        Map<String, User> userMap = new HashMap<>();
        userMap.put("user", user);
        String responseValue ="";
        try {
            String restUrl = restConfig.getRegisterUrl();
            SSLContext sslContext = restConfig.createSSLContext();

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            HttpHeaders headers = restConfig.configureHttpHeaders();

            HttpEntity<Map<String, User>> entity = new HttpEntity<Map<String, User>>(userMap, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new MyResponseErrorHandler());
            ResponseEntity<String> response = restTemplate.postForEntity(restUrl, entity, String.class);
            responseValue = response.getBody();
            logger.debug("status response: " + response.getBody());
            logger.debug("StatusCode : " + response.getStatusCode());
            logger.debug("StatusCodeValue : " + response.getStatusCodeValue());
            logger.debug("response headers : " + response.getHeaders());
            if (response.getStatusCodeValue() != 201){
                throw new LogicalException("error occured while user registration");
            }

        } catch(HttpStatusCodeException e){
            String errorPayload = e.getResponseBodyAsString();
            logger.error("errorPayload : " + errorPayload);
            throw new LogicalException(errorPayload);
        } catch (RestClientException ex) {
            ex.printStackTrace();
            logger.error("message : " + ex.getMessage());
            throw new LogicalException(ex.getMessage());
        }

        System.out.println("registered user :" + user);
        return responseValue;
    }

    @Override
    public User findByUsername(String username) {
        // TODO Auto-generated method stub
        return null;
    }

    private Map<String, User> getUserMap(User inputUser) {
        Map<String, User> userMap = new HashMap<>();
        User user = new User();
        user.setUsername(inputUser.getUsername());
        user.setRealm(inputUser.getRealm());
        user.setPassword(inputUser.getPassword());
        user.setPasswordConfirm(inputUser.getPasswordConfirm());
        if (!CollectionUtils.isEmpty(inputUser.getClaims())) {
            user.setClaims(inputUser.getClaims());
        }
        if (!CollectionUtils.isEmpty(inputUser.getProperties())) {
            user.setProperties(inputUser.getProperties());
        }
        userMap.put("user", user);
        return userMap;
    }

}
