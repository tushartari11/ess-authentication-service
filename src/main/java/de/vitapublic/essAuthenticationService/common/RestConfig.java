package de.vitapublic.essAuthenticationService.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Base64;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public class RestConfig {

	@Value("${ess.services.security.key-store}")
	private String keyStoreFile;

	@Value("${ess.services.security.key-store-password}")
	private String keyStorePassword;

	@Value("${wso2is.host-address}") // centos.vagrant
	private String hostAddress;

	@Value("${wso2is.host-scheme}") // https://
	private String scheme;

	@Value("${wso2is.host-port}") // 9443
	private String hostPort;

	@Value("${wso2is.register.user.uri}") // /api/identity/user/v0.9/me
	private String registerUri;
	
	@Value("${wso2is.oauth2.token.uri}") // Bearer Token
	private String oauthTokeUri;


	@Value("${wso2is.client.id_token}") // Bearer Token
	private String clientToken;

	public SSLContext createSSLContext() {
		SSLContext sslContext = null;
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(new FileInputStream(new File(keyStoreFile)), keyStorePassword.toCharArray());
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
					.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sslContext;
	}

	public String getRegisterUrl() {
		String restUrl = scheme + hostAddress + ":" + hostPort + registerUri;
		return restUrl;
	}

	public String getOauthTokenUri() {
		String oauthTokenUri = scheme + hostAddress + ":" + hostPort + oauthTokeUri;
		return oauthTokenUri;
	}
	
	public String getBearerToken() {
		return clientToken;
	}

	public String getBasicClientToken(String clientKey, String clientSecret) throws UnsupportedEncodingException {
		String encodedClientId;
		String clientId = clientKey + ":" + clientSecret;
		encodedClientId = Base64.getEncoder().encodeToString(clientId.getBytes("utf-8"));
		return encodedClientId;
	}
	/**
	 * @return http request Headers
	 */
	public HttpHeaders configureHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Bearer "+clientToken);
		return headers;
	}

	public HttpHeaders configureHttpHeadersForAuthToken(String clientKey, String clientSecret) throws UnsupportedEncodingException {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Basic "+getBasicClientToken(clientKey,clientSecret));
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		return headers;
	}
}
