package de.vitapublic.essAuthenticationService.security.authorization;

import de.vitapublic.essAuthenticationService.security.exceptions.JwtInvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AudienceProvider {

    private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    private RestTemplate restTemplate;
    private RedisTemplate redisTemplate;

    private static final String AUDIENCE_REDIS_KEY = "ess_organizer_service:data.audience";
    static final Long ADMIN_AUDIENCE_ID = -1L;
    static final Long SYSTEM_AUDIENCE_ID = -2L;

    @Value("${ess.services.url.organizer.audience:}")
    private String audienceEndpointUrl;

    @Value("${ess.services.rest-template.api-key:}")
    private String organizerServiceToken;

    @Autowired
    public AudienceProvider(
            RestTemplate restTemplate,
            @Qualifier("redisTemplate") RedisTemplate redisTemplate
    ) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        if (!audienceEndpointUrl.endsWith("/")) {
            audienceEndpointUrl = audienceEndpointUrl + "/";
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getAudience(String audience) {
        Map<String, Object> audienceObject = (Map<String, Object>) redisTemplate.opsForHash().get(AUDIENCE_REDIS_KEY, audience);
        if (null == audienceObject) {
            try {
                ResponseEntity entity = restTemplate.exchange(getRequestUrl(audience), HttpMethod.GET, getRequest(), HashMap.class);
                if (entity.getStatusCode().is2xxSuccessful()) {
                    audienceObject = (HashMap<String, Object>) entity.getBody();
                    if (audienceObject.get("id") instanceof Integer) {
                        audienceObject.put("id", ((Integer) audienceObject.get("id")).longValue());
                    }
                } else {
                    throw new JwtInvalidTokenException("Invalid Audience");
                }
            } catch (RestClientException ex) {
                throw new JwtInvalidTokenException("Invalid Audience");
            }
        }
        return audienceObject;
    }

    private String getRequestUrl(String audience) {
        return audienceEndpointUrl + audience;
    }

    private HttpEntity getRequest() {
        return new HttpEntity<>(getRequestHeader());
    }

    private HttpHeaders getRequestHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (null != organizerServiceToken && !organizerServiceToken.isEmpty()) {
            headers.set("Authorization", AUTHORIZATION_HEADER_PREFIX + organizerServiceToken);
        }
        return headers;
    }
}
