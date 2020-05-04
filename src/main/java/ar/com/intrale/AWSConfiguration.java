package ar.com.intrale;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

	@Value("${jwtUrl:'./jwks.json'}")
	private String jwtUrl;

	@Bean
	@ConditionalOnProperty(
		    value="authorizer.enabled", 
		    havingValue = "true", 
		    matchIfMissing = false)
    public ConfigurableJWTProcessor configurableJWTProcessor() throws MalformedURLException {
        ResourceRetriever resourceRetriever = 
             new DefaultResourceRetriever(connectionTimeout, readTimeout);
	    //https://cognito-idp.{region}.amazonaws.com/{userPoolId}/.well-known/jwks.json.
	    URL jwkSetURL= new URL(jwtUrl);
	    //Creates the JSON Web Key (JWK)
	    JWKSource keySource= new RemoteJWKSet(jwkSetURL, resourceRetriever);
	    ConfigurableJWTProcessor jwtProcessor= new DefaultJWTProcessor();
	    //RSASSA-PKCS-v1_5 using SHA-256 hash algorithm
	    JWSKeySelector keySelector= new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
	    jwtProcessor.setJWSKeySelector(keySelector);
	    return jwtProcessor;
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

