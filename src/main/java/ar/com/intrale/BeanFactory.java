package ar.com.intrale;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import ar.com.intrale.config.ApplicationConfig;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class BeanFactory {
	
	public static final String USER_POOL_ID_URL = "userPoolIdUrl";
	
	@Inject
	protected ApplicationConfig config;

	@Bean @Singleton
	public ObjectMapper ObjectMapper() {
		return new ObjectMapper();
	}
	
	@Bean @Singleton @Named(USER_POOL_ID_URL)
	public String getUserPoolIdUrl() {
		StringBuilder jwtUrl = new StringBuilder();
		jwtUrl.append(config.getCognito().getUrlPrefix());
		jwtUrl.append(config.getAws().getRegion());
		jwtUrl.append(config.getCognito().getUrlMid());
		jwtUrl.append(config.getCognito().getUserPoolId());
		jwtUrl.append(config.getCognito().getUrlSufix());
		return jwtUrl.toString();
	}
	
	@Bean @Singleton
	public ConfigurableJWTProcessor JWTProcessor() throws MalformedURLException {
		ResourceRetriever resourceRetriever = new DefaultResourceRetriever(config.getCognito().getConnectionTimeout(), config.getCognito().getReadTimeout());
		
		StringBuilder jwtUrl = new StringBuilder();
		jwtUrl.append(getUserPoolIdUrl());
	    URL jwkSetURL= new URL(jwtUrl.toString());
	    
	    //Creates the JSON Web Key (JWK)
	    JWKSource keySource= new RemoteJWKSet(jwkSetURL, resourceRetriever);
	  
	    //RSASSA-PKCS-v1_5 using SHA-256 hash algorithm
	    JWSKeySelector keySelector= new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
	    
	    ConfigurableJWTProcessor processor = new DefaultJWTProcessor();
	    processor.setJWSKeySelector(keySelector);
	    return processor;
	}
	
	
}
