package ar.com.intrale.in;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import ar.com.intrale.AWSConfiguration;

@Component
public class Authorizer {
	
	public static final String AUTHORIZATION = "Authorization";

	@Autowired(required = false)
	private ConfigurableJWTProcessor processor;
	
	@Value("${authorizer.enabled}")
	private Boolean enabled;
	
	@Autowired
	private AWSConfiguration config;
	
	public AuthorizationResult validate (String reference, String authorization) throws ParseException, BadJOSEException, JOSEException {
		if (enabled ) {
			if (authorization!=null){
				String jwt = authorization.substring("Bearer ".length());

				JWTClaimsSet claimsSet = processor.process(jwt, null);
				
				if ((!isIssuedCorrectly(claimsSet)) || (!isIdToken(claimsSet))) {
					 return new AuthorizationResult(Boolean.FALSE, new InvalidTokenErrorResponse());
			    }
			
		
			} else {
				return new AuthorizationResult(Boolean.FALSE, new NotAuthorizationFoundErrorResponse());
			}
		}
		return new AuthorizationResult(Boolean.TRUE, null);
	}
	
	private boolean isIssuedCorrectly(JWTClaimsSet claimsSet) {
		// aca usaba la url en lugar del id del pool
	       return claimsSet.getIssuer().equals(config.getUserPoolId());
	}
	 
	private boolean isIdToken(JWTClaimsSet claimsSet) {
	       return claimsSet.getClaim("token_use").equals("id");
	}
	
}
