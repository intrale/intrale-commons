package ar.com.intrale.in;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import ar.com.intrale.AWSConfiguration;

@Component
@ConditionalOnProperty(
	    value="controller.enabled")
public class JWTProcessor extends DefaultJWTProcessor {

	private static final String COGNITO_URL_PREFIX = "https://cognito-idp.";
	private static final String COGNITO_URL_MID = ".amazonaws.com/";
	private static final String COGNITO_URL_SUFIX = "/.well-known/jwks.json";
	
	@Value("${connectionTimeout:2000}")
	private Integer connectionTimeout;
	
	@Value("${readTimeout:2000}")
	private Integer readTimeout;
	
	@Autowired
	private AWSConfiguration config;
	
	public JWTProcessor() throws MalformedURLException {
		super();
	
		System.out.println("JWTProcessor:" + connectionTimeout + ", " + readTimeout);
		ResourceRetriever resourceRetriever = new DefaultResourceRetriever(connectionTimeout, readTimeout);
		
		System.out.println("JWTProcessor: Build URL");
		StringBuilder jwtUrl = new StringBuilder();
		jwtUrl.append(COGNITO_URL_PREFIX).append(config.getRegion()).append(COGNITO_URL_MID).append(config.getUserPoolId()).append(COGNITO_URL_SUFIX);
		System.out.println("JWTProcessor:" + jwtUrl.toString());
	    URL jwkSetURL= new URL(jwtUrl.toString());
	    
	    System.out.println("JWTProcessor: keySource");
	    //Creates the JSON Web Key (JWK)
	    JWKSource keySource= new RemoteJWKSet(jwkSetURL, resourceRetriever);
	  
	    System.out.println("JWTProcessor: keySelector");
	    //RSASSA-PKCS-v1_5 using SHA-256 hash algorithm
	    JWSKeySelector keySelector= new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
	    
	    System.out.println("JWTProcessor: setJWSKeySelector");
	    setJWSKeySelector(keySelector);
	    
	    System.out.println("JWTProcessor: finish constructor");
	}

}
