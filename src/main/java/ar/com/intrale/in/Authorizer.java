package ar.com.intrale.in;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import ar.com.intrale.AWSConfiguration;

@Component
public class Authorizer {
	
	private static final String COGNITO_GROUPS = "cognito:groups";

	private static final String TOKEN_USE = "token_use";

	public static final String AUTHORIZATION = "Authorization";

	@Autowired
	private ConfigurableJWTProcessor processor;
	
	@Value("${authorizer.enabled}")
	private Boolean enabled;
	
	@Value("${authorizer.group:'SIN_GRUPO'}")
	private String group;
	
	@Autowired
	private AWSConfiguration config;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public void validate (String authorization) throws BeansException, IntraleFunctionException {
		if (enabled ) {
			
			if (authorization!=null){
				String jwt = authorization.substring("Bearer ".length());
				JWTClaimsSet claimsSet = null;
				try {
					claimsSet = processor.process(jwt, null);
				} catch (BadJWTException e) {
					if (e.getMessage().contains("Expired")) {
						throwException(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
					}
					throwException(HttpStatus.BAD_REQUEST, "BAD_TOKEN");
				} catch (Exception e) {
					throwException(HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_EXCEPTION");
				} 
				
				if ((!isCorrectUserPool(claimsSet)) || (!isCorrectTokenUse(claimsSet, "access"))) {
					throwException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
			    }
				
				// Validando si el usuario pertenece al grupo que tiene permitido ejecutar esta accion
				List groups = (List) claimsSet.getClaims().get(COGNITO_GROUPS);
				if ((groups==null) || (!groups.contains(group))) {
					throwException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
				}
			} else {
				throwException(HttpStatus.UNAUTHORIZED, "NOT_AUTHORIZATION_FOUND");
			}
			
		}
	}
	
	public void throwException(HttpStatus status, String description) throws BeansException, IntraleFunctionException {
		throw (IntraleFunctionException) applicationContext.getBean(IntraleFunctionException.NAME, status, description);
	}
	
	
	
	private boolean isCorrectUserPool(JWTClaimsSet claimsSet) {
       return claimsSet.getIssuer().equals(config.getUserPoolIdUrl());
	}
	 
	private boolean isCorrectTokenUse(JWTClaimsSet claimsSet, String tokenUseType) {
	       return claimsSet.getClaim(TOKEN_USE).equals(tokenUseType);
	}
	
}
