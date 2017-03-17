package de.vitapublic.essAuthenticationService.service;

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
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.util.HashMap;
import java.util.Map;

public class ClientServiceImpl implements  ClientService{

    @Autowired
    RestConfig restConfig;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public String authenticateClient(String clientKey, String clientSecret) throws LogicalException {

            String responseValue ="";
            try {
                String restUrl = restConfig.getRegisterUrl();
                SSLContext sslContext = restConfig.createSSLContext();

                HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

                HttpHeaders headers = restConfig.configureHttpHeadersForAuthToken(clientKey,clientSecret);

                HttpEntity<Map<String, User>> entity = new HttpEntity<Map<String, User>>(new HashMap<>(), headers);

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
            }catch (Exception ex){
                ex.printStackTrace();
                logger.error("message : " + ex.getMessage());
                throw new LogicalException(ex.getMessage());
            }

            System.out.println("registered client :" + responseValue);
            return responseValue;
        }

}
