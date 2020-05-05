package ar.com.intrale.in;

import java.net.MalformedURLException;
import java.net.URL;

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

@Component
@ConditionalOnProperty(
	    value="controller.enabled")
public class JWTProcessor extends DefaultJWTProcessor {

	@Value("${connectionTimeout:2000}")
	private Integer connectionTimeout;
	
	@Value("${readTimeout:2000}")
	private Integer readTimeout;

	@Value("${jwtUrl:'./jwks.json'}")
	private String jwtUrl;
	
	public JWTProcessor() throws MalformedURLException {
		super();
		
		ResourceRetriever resourceRetriever = new DefaultResourceRetriever(connectionTimeout, readTimeout);
	    //https://cognito-idp.{region}.amazonaws.com/{userPoolId}/.well-known/jwks.json.
	    URL jwkSetURL= new URL(jwtUrl);

	    ConfigurableJWTProcessor jwtProcessor= new DefaultJWTProcessor();
	    
	    //Creates the JSON Web Key (JWK)
	    JWKSource keySource= new RemoteJWKSet(jwkSetURL, resourceRetriever);
	  
	    //RSASSA-PKCS-v1_5 using SHA-256 hash algorithm
	    JWSKeySelector keySelector= new JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource);
	    
	    setJWSKeySelector(keySelector);
	}

}
