package ar.com.intrale;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

@Configuration
@PropertySource("classpath:AwsCredentials.properties")
public class AWSConfiguration {
	
	private static final String COGNITO_URL_PREFIX = "https://cognito-idp.";
	private static final String COGNITO_URL_MID = ".amazonaws.com/";
	private static final String COGNITO_URL_SUFIX = "/.well-known/jwks.json";

	@Value("${userPoolId}")
	private String userPoolId;
	
	@Value("${region}")
	private String region;
	
	@Value("${clientId}")
	private String clientId;
	
	@Value("${connectionTimeout:2000}")
	private Integer connectionTimeout;
	
	@Value("${readTimeout:2000}")
	private Integer readTimeout;
	
	@Bean
	public ConfigurableJWTProcessor JWTProcessor() throws MalformedURLException {
	
		System.out.println("JWTProcessor:" + connectionTimeout + ", " + readTimeout);
		ResourceRetriever resourceRetriever = new DefaultResourceRetriever(connectionTimeout, readTimeout);
		
		System.out.println("JWTProcessor: Build URL");
		StringBuilder jwtUrl = new StringBuilder();
		jwtUrl.append(COGNITO_URL_PREFIX).append(region).append(COGNITO_URL_MID).append(userPoolId).append(COGNITO_URL_SUFIX);
		System.out.println("JWTProcessor:" + jwtUrl.toString());
	    URL jwkSetURL= new URL(jwtUrl.toString());
	    
	    System.out.println("JWTProcessor: keySource");
	    //Creates the JSON Web Key (JWK)
	    JWKSource keySource= new RemoteJWKSet(jwkSetURL, resourceRetriever);
	  
	    System.out.println("JWTProcessor: keySelector");
	    //RSASSA-PKCS-v1_5 using SHA-256 hash algorithm
	    JWSKeySelector keySelector= new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
	    
	    System.out.println("JWTProcessor: setJWSKeySelector");
	    ConfigurableJWTProcessor processor = new DefaultJWTProcessor();
	    processor.setJWSKeySelector(keySelector);
	    
	    System.out.println("JWTProcessor: finish constructor");
	    return processor;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public Integer getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getUserPoolId() {
		return userPoolId;
	}
	public void setUserPoolId(String userPoolId) {
		this.userPoolId = userPoolId;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	
	
}

