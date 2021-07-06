package ar.com.intrale.cloud;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import ar.com.intrale.cloud.config.ApplicationConfig;
import ar.com.intrale.cloud.exceptions.BadRequestException;
import ar.com.intrale.cloud.exceptions.BusinessNotFoundException;
import ar.com.intrale.cloud.exceptions.EmptyRequestException;
import ar.com.intrale.cloud.exceptions.FunctionException;
import ar.com.intrale.cloud.exceptions.TokenNotFoundException;
import ar.com.intrale.cloud.exceptions.UnauthorizeExeption;
import ar.com.intrale.cloud.exceptions.UnexpectedException;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.validation.validator.Validator;


public abstract class IntraleFunction<REQ extends Request, RES extends Response, PROV> {
	
	private static final String ID = "id";

	private static final String USERNAME = "username";

	public static final String TOKEN_NOT_FOUND = "TOKEN_NOT_FOUND";
	
	public static final String BUSINESS_NOT_FOUND = "BUSINESS_NOT_FOUND";

	public static final String NOT_AUTHORIZATION_FOUND = "NOT_AUTHORIZATION_FOUND";

	public static final String UNAUTHORIZED = "UNAUTHORIZED";

	public static final String INVALID_TOKEN = "INVALID_TOKEN";

	public static final String ACCESS = "access";

	public static final String UNEXPECTED_EXCEPTION = "UNEXPECTED_EXCEPTION";

	public static final String BAD_TOKEN = "BAD_TOKEN";

	public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";

	public static final String UNEXPECTED = "UNEXPECTED";

	public static final String EMPTY_REQUEST = "EMPTY_REQUEST";

	public static final String EXPIRED = "Expired";

	public static final String BEARER = "Bearer ";
	
	public static final String BUSINESS_ATTRIBUTE = "profile";
	
	public static final String BUSINESS_ATTRIBUTE_SEPARATOR = ",";

	private static final Logger LOGGER = LoggerFactory.getLogger(IntraleFunction.class);
	
	public static final String TRUE = "true";
	
	public static final String APP_INSTANTIATE = "app.instantiate.";
	
	public static final String BUSINESS_NAME 	= "businessName";
	
	private static final String COGNITO_GROUPS = "cognito:groups";
	private static final String TOKEN_USE = "token_use";

    public static final String NUMERAL = "#";
    public static final String TWO_POINTS = ":";
    
	public static final String CREATE = "create";
	public static final String READ = "read";
	public static final String UPDATE = "update";
	public static final String DELETE = "delete";
	public static final String SAVE = "save";
	
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
	
	@Inject
	protected ConfigurableJWTProcessor processor;
	
	@Inject @Named(BeanFactory.USER_POOL_ID_URL)
	protected String userPoolIdUrl;

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
	
	public HttpResponse<String> apply(Map <String, String> headers, String request) {
		
    	LOGGER.info("INTRALE: iniciando funcion");
    	LOGGER.info("INTRALE: request => \n" + request);
    	try {
    		validate(headers);
    		
    		REQ requestObject = (REQ) build(headers, request, requestType);
	    	
	    	if (StringUtils.isEmpty(request)) {
	    		return HttpResponse.badRequest();
	    	}
	    	
	    	RES res = execute(requestObject);
	    	res.setStatusCode(200);
	    	
	    	return logResponse(HttpResponse.ok().body(mapper.writeValueAsString(res)));
		} catch (FunctionException e) {
			return logResponse(e.getResponse());
		} catch (Exception e) {
			LOGGER.error(FunctionException.toString(e));
		}
		return logResponse(HttpResponse.serverError());
        
	}

	
	private HttpResponse<String> logResponse(HttpResponse<String> response) {
		LOGGER.info("INTRALE: response => \n" + response.body());
		return response;
	}
	
	public abstract RES execute (REQ request) throws FunctionException;

	
	protected Request build(Map <String, String> headers, String request, Class<Request> requestType) throws FunctionException {  	
		if (StringUtils.isEmpty(request)) {
    		throw new EmptyRequestException(new Error(EMPTY_REQUEST, EMPTY_REQUEST), mapper);
    	}
    	
		Request requestObject = null;
		try {
			requestObject = (Request) mapper.readValue(request, requestType);
		} catch (JsonProcessingException e) {
			throw new UnexpectedException(new Error(UNEXPECTED, e.getMessage()), mapper);
		}
		
    	validateRequestBuilded(requestObject);

    	requestObject.setHeaders(headers);
    	
    	return requestObject;
	}

	protected void validateRequestBuilded(Request requestObject) throws BadRequestException {
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
	}
	
    public PROV getProvider() {
		return provider;
	}

	public void setProvider(PROV provider) {
		this.provider = provider;
	}

	// Segurizacion de la funcion
	/**
	 * Retorna el nombre del grupo que deberia tener el perfil de usuario que intenta ejecutar la funcion
	 * @return
	 */
	public String getFunctionGroup() {
		return null;
	}

	protected boolean isSecurityEnabled() {
		return !StringUtils.isEmpty(getFunctionGroup());
	}
	
	public void validate (Map <String, String> headers) throws FunctionException {
		LOGGER.info("INTRALE: inicio validate");
		
		String authorization = headers.get(Lambda.HEADER_AUTHORIZATION);
		String idToken = headers.get(Lambda.HEADER_ID_TOKEN);
		String businessName = headers.get(Lambda.HEADER_BUSINESS_NAME);

		if (StringUtils.isEmpty(businessName)) {
			throw new BusinessNotFoundException(new Error(BUSINESS_NOT_FOUND, BUSINESS_NOT_FOUND), mapper);
		}
		
		if (isSecurityEnabled()) {
			if (authorization!=null){
				
				JWTClaimsSet authClaimsSet = validateToken(authorization, ACCESS);
				JWTClaimsSet idTokenClaimsSet = validateToken(idToken, ID);
				
				//Se valida que el usuario este registrado para la organizacion / negocio que desea ejecutar la funcion
				String username = (String) authClaimsSet.getClaims().get(USERNAME);
				String businessNames = (String) idTokenClaimsSet.getClaims().get(BUSINESS_ATTRIBUTE);
				List<String> businessNamesRegistered = Arrays.asList(businessNames.split(BUSINESS_ATTRIBUTE_SEPARATOR));
				if (!businessNamesRegistered.contains(businessName)) {
					throw new UnauthorizeExeption(new Error(UNAUTHORIZED, "User " + username + " Unauthorized for business " + businessName), mapper);
				}
				
				LOGGER.info("INTRALE: username on token:" + username);
				
				// Validando si el usuario pertenece al grupo que tiene permitido ejecutar esta accion
				List groups = getGroups(authClaimsSet);
				if  ((!StringUtils.isEmpty(getFunctionGroup())) &&
						((groups==null) || (!groups.contains(getFunctionGroup())))){
					throw new UnauthorizeExeption(new Error(UNAUTHORIZED, UNAUTHORIZED), mapper);
				}
				
			} else {
				throw new UnauthorizeExeption(new Error(NOT_AUTHORIZATION_FOUND, NOT_AUTHORIZATION_FOUND), mapper);
			}
			
		}
		LOGGER.info("INTRALE: fin validate");
	}

	protected List getGroups(JWTClaimsSet claimsSet) {
		LOGGER.info("INTRALE: inicio getGroups");
		Map<String, Object> claims = claimsSet.getClaims();
		
		List groups = null;
		if (claims!=null && claims.size()>0) {
		 groups = (List) claimsSet.getClaims().get(COGNITO_GROUPS);
		}
		LOGGER.info("INTRALE: fin getGroups");
		return groups;
	}

	private JWTClaimsSet validateToken(String token, String tokenUse)
			throws FunctionException {
		LOGGER.info("INTRALE: inicio validateToken");
		if (StringUtils.isEmpty(token)) {
			throw new TokenNotFoundException(new Error(TOKEN_NOT_FOUND, TOKEN_NOT_FOUND), mapper);
		}
		
		String jwt = token;
		if (token.contains(BEARER)) {
			jwt = token.substring(BEARER.length());
		}
		
		JWTClaimsSet claimsSet = null;
		try {
			claimsSet = processor.process(jwt, null);
		} catch (BadJWTException e) {
			if (e.getMessage().contains(EXPIRED)) {
				LOGGER.info("INTRALE: token expired");
				throw new UnauthorizeExeption(new Error(TOKEN_EXPIRED, TOKEN_EXPIRED), mapper);
			}
			LOGGER.info("INTRALE: bad token");
			throw new BadRequestException(new Error(BAD_TOKEN, BAD_TOKEN), mapper);
		} catch (Exception e) {
			LOGGER.error("INTRALE: unexpected exception");
			LOGGER.error("INTRALE: authorization:" + token);
			LOGGER.error(FunctionException.toString(e));
			throw new UnexpectedException(new Error(UNEXPECTED_EXCEPTION, UNEXPECTED_EXCEPTION), mapper);
		} 
		
		if ((!isCorrectUserPool(claimsSet)) || 
				(!isCorrectTokenUse(claimsSet, tokenUse))) {
			LOGGER.info("INTRALE: invalid token");
			throw new UnauthorizeExeption(new Error(INVALID_TOKEN, INVALID_TOKEN), mapper);
		}
		
		LOGGER.info("INTRALE: fin validateToken");
		
		return claimsSet;
	}
	
	private boolean isCorrectUserPool(JWTClaimsSet claimsSet) {
       return userPoolIdUrl.contains(claimsSet.getIssuer());
	}
	 
	private boolean isCorrectTokenUse(JWTClaimsSet claimsSet, String tokenUseType) {
	       return claimsSet.getClaim(TOKEN_USE).equals(tokenUseType);
	}
	
	
}
