package ar.com.intrale.in;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import ar.com.intrale.AWSConfiguration;

@Component
public class Authorizer {
	
	private static final String TOKEN_USE = "token_use";

	public static final String AUTHORIZATION = "Authorization";

	@Autowired
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
				
				if ((!isCorrectUserPool(claimsSet)) || (!isCorrectTokenUse(claimsSet, "access"))) {
					 return new AuthorizationResult(Boolean.FALSE, new InvalidTokenErrorResponse());
			    }
				
				claimsSet.getClaims();
			
		
			} else {
				return new AuthorizationResult(Boolean.FALSE, new NotAuthorizationFoundErrorResponse());
			}
			
		}
		return new AuthorizationResult(Boolean.TRUE, null);
	}
	
	
	
	private boolean isCorrectUserPool(JWTClaimsSet claimsSet) {
       return claimsSet.getIssuer().equals(config.getUserPoolIdUrl());
	}
	 
	private boolean isCorrectTokenUse(JWTClaimsSet claimsSet, String tokenUseType) {
	       return claimsSet.getClaim(TOKEN_USE).equals(tokenUseType);
	}
	
}
