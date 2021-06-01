package ar.com.intrale.cloud;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;

import ar.com.intrale.cloud.config.ApplicationConfig;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.validation.validator.Validator;


public abstract class Function<REQ extends Request, RES extends Response, PROV> implements java.util.function.Function<String, HttpResponse<String>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Function.class);
	
	public static final String TRUE = "true";
	public static final String APP_INSTANTIATE = "app.instantiate.";
	
	public static final String BUSINESS_NAME 	= "businessName";

    public static final String NUMERAL = "#";
    public static final String TWO_POINTS = ":";
    
	public static final String CREATE = "create";
	public static final String READ = "read";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";
	
	private final Class<Request> requestType = (Class<Request>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	private final Class<Request> providerType = (Class<Request>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	
	@Inject
   	protected Validator validator;
	
   	@Inject
   	protected ObjectMapper mapper;
   	
   	@Inject
   	protected ApplicationContext applicationContext;
   	
   	protected PROV provider;
   	
	@Inject
	protected ApplicationConfig config;

   	public Class<Request> getProviderType() {
		return providerType;
	}
	
	public ApplicationConfig getConfig() {
		return config;
	}

	@PostConstruct
    public Boolean postConstruct() {
		try {
			provider = (PROV) applicationContext.getBean(providerType);
		} catch (Exception e) {
			LOGGER.info("INTRALE: No fue posible inicializar el provider:" + providerType);
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
    }
	
	@Override
	public HttpResponse<String> apply(String request) {
		
    	LOGGER.info("INTRALE: iniciando funcion");
    	LOGGER.info("INTRALE: request => \n" + request);
    	try {
    		REQ requestObject = (REQ) build(request, requestType);
	    	
	    	if (StringUtils.isEmpty(request)) {
	    		return HttpResponse.badRequest();
	    	}
	    	
	    	RES res = execute(requestObject);
	    	res.setStatusCode(200);
	    	
	    	return logResponse(HttpResponse.ok().body(mapper.writeValueAsString(res)));
		} catch (FunctionException e) {
			return logResponse(e.getResponse());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return logResponse(HttpResponse.serverError());
        
	}

	
	private HttpResponse<String> logResponse(HttpResponse<String> response) {
		LOGGER.info("INTRALE: response => \n" + response.body());
		return response;
	}
	
	public abstract RES execute (REQ request) throws FunctionException;

	
	protected Request build(String request, Class<Request> requestType) throws FunctionException {  	
		if (StringUtils.isEmpty(request)) {
    		throw new EmptyRequestException(new Error("EMPTY_REQUEST", "EMPTY_REQUEST"), mapper);
    	}
    	
		Request requestObject = null;
		try {
			requestObject = (Request) mapper.readValue(request, requestType);
		} catch (JsonProcessingException e) {
			throw new UnexpectedException(new Error("UNEXPECTED", e.getMessage()), mapper);
		}
		
    	Collection<Error> errors = new ArrayList<Error>();
    	
    	Set<ConstraintViolation<Request>> validatorErrors = validator.validate(requestObject, Default.class);
    	if (validatorErrors.size()>0) {
	    	Iterator<ConstraintViolation<Request>> it = validatorErrors.iterator();
	    	while (it.hasNext()) {
				ConstraintViolation<ar.com.intrale.cloud.Request> constraintViolation = (ConstraintViolation<ar.com.intrale.cloud.Request>) it.next();
				errors.add(new Error(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
			}
	    	LOGGER.info("retornando error");
	    	throw new BadRequestException(errors, mapper);
    	}

    	return requestObject;
	}
	
    public PROV getProvider() {
		return provider;
	}

	public void setProvider(PROV provider) {
		this.provider = provider;
	}

	// Segurizacion de la funcion
	/**
	 * Retorna verdadero si es necesario validar la seguridad para la funcion
	 * @return
	 */
	public abstract Boolean getSecurityEnabled();
	
	public void validate (String authorization) {
		/*if (getSecurityEnabled()) {
			
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
			
		}*/
	}
	
	/*public void throwException(HttpStatus status, String description) throws BeansException, IntraleFunctionException {
		throw new IntraleFunctionException (status, description);
	}
	
	
	
	private boolean isCorrectUserPool(JWTClaimsSet claimsSet) {
       return claimsSet.getIssuer().equals(config.getUserPoolIdUrl());
	}
	 
	private boolean isCorrectTokenUse(JWTClaimsSet claimsSet, String tokenUseType) {
	       return claimsSet.getClaim(TOKEN_USE).equals(tokenUseType);
	}*/
	
	
}
