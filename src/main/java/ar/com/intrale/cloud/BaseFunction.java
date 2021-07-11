package ar.com.intrale.cloud;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import ar.com.intrale.cloud.config.ApplicationConfig;
import ar.com.intrale.cloud.exceptions.BadRequestException;
import ar.com.intrale.cloud.exceptions.BusinessNotFoundException;
import ar.com.intrale.cloud.exceptions.FunctionException;
import ar.com.intrale.cloud.exceptions.TokenNotFoundException;
import ar.com.intrale.cloud.exceptions.UnauthorizeExeption;
import ar.com.intrale.cloud.exceptions.UnexpectedException;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.validation.validator.Validator;

/**
 * Funcion cuya entrada y salida esta en formato JSON
 */
public abstract class BaseFunction<	FUNCTION_REQ, 
									FUNCTION_RES, 
									PROV, 
									REQ_BUILDER extends Builder,
									RES_BUILDER extends BuilderForLambda> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseFunction.class);

	protected final Class<Request> providerType = (Class<Request>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	protected final Class<Request> requestBuilderType = (Class<Request>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];
	protected final Class<Request> responseBuilderType = (Class<Request>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[4];
	
	@Inject
   	protected Validator validator;
	
   	@Inject
   	protected ApplicationContext applicationContext;
   	
	@Inject
	protected ApplicationConfig config;
	
   	@Inject
   	protected ObjectMapper mapper;
   	
	@Inject
	protected ConfigurableJWTProcessor processor;
	
	@Inject @Named(BeanFactory.USER_POOL_ID_URL)
	protected String userPoolIdUrl;
	
	protected PROV provider;
	
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
	
	public Object msApply(Map <String, String> headers, Object request) {
		
    	LOGGER.info("INTRALE: iniciando funcion");
    	LOGGER.info("INTRALE: request => \n" + request);
    	
    	try {
    		
    		validate(headers);
    		
    		FUNCTION_REQ requestObject = (FUNCTION_REQ) buildRequest(headers, request);
	    	
	    	FUNCTION_RES res = execute(requestObject);
	    	//res.setStatusCode(200);
	    	
	    	return buildResponse(res);
	    	
		} catch (FunctionException e) {
			HttpResponse<String> response = e.getResponse();
			LOGGER.info("INTRALE: response => \n" + response.body());
			return response;
		} catch (Exception e) {
			LOGGER.error(FunctionException.toString(e));
		}
    	
    	HttpResponse<String> response = HttpResponse.serverError();
    	LOGGER.info("INTRALE: response => \n" + response.body());
		return response;
        
	}
	
	public Object lambdaApply(Map <String, String> headers, Object request) {
		String body = StringUtils.EMPTY_STRING;
		if (request instanceof APIGatewayProxyRequestEvent) {
			body = ((APIGatewayProxyRequestEvent) request).getBody();
		}
		
		Object response = msApply(headers, body);
		RES_BUILDER builder = (RES_BUILDER) applicationContext.getBean(responseBuilderType);
		return builder.wrapForLambda(response);
		
		/*HttpResponse<String> response =  (HttpResponse<String>) msApply(headers, request.getBody());
    	APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
    	
		// CORS avaiable
		Map<String, String> responseHeaders = new HashMap<String, String>();
		responseHeaders.put(FunctionConst.ACCESS_CONTROL_ALLOW_ORIGIN, FunctionConst.ALL);
		responseHeaders.put(FunctionConst.ACCESS_CONTROL_ALLOW_METHODS, FunctionConst.GET_OPTIONS_HEAD_PUT_POST);
		responseEvent.setHeaders(responseHeaders); 
		
    	responseEvent.setStatusCode(response.getStatus().getCode());
    	responseEvent.setBody(response.body());
    	responseEvent.setIsBase64Encoded(Boolean.TRUE);
    	
    	return responseEvent;*/
	}
	
	private Object buildResponse(FUNCTION_RES res) throws Exception {
		RES_BUILDER builder = (RES_BUILDER) applicationContext.getBean(responseBuilderType);
		return builder.build(null, res);
	}
	
	protected FUNCTION_REQ buildRequest(Map <String, String> headers, Object request) throws FunctionException {  	
		REQ_BUILDER builder = (REQ_BUILDER) applicationContext.getBean(requestBuilderType);
		return (FUNCTION_REQ) builder.build(headers, request);
	}
	
	public abstract FUNCTION_RES execute (FUNCTION_REQ request) throws FunctionException;
	
	// Segurizacion de la funcion
	
	public void validate (Map <String, String> headers) throws FunctionException {
		LOGGER.info("INTRALE: inicio validate");
		
		String authorization = headers.get(FunctionBuilder.HEADER_AUTHORIZATION);
		String idToken = headers.get(FunctionBuilder.HEADER_ID_TOKEN);
		String businessName = headers.get(FunctionBuilder.HEADER_BUSINESS_NAME);

		if (StringUtils.isEmpty(businessName)) {
			throw new BusinessNotFoundException(new Error(FunctionConst.BUSINESS_NOT_FOUND, FunctionConst.BUSINESS_NOT_FOUND), mapper);
		}
		
		if (isSecurityEnabled()) {
			if (authorization!=null){
				
				JWTClaimsSet authClaimsSet = validateToken(authorization, FunctionConst.ACCESS);
				JWTClaimsSet idTokenClaimsSet = validateToken(idToken, FunctionConst.ID);
				
				//Se valida que el usuario este registrado para la organizacion / negocio que desea ejecutar la funcion
				String username = (String) authClaimsSet.getClaims().get(FunctionConst.USERNAME);
				String businessNames = (String) idTokenClaimsSet.getClaims().get(FunctionConst.BUSINESS_ATTRIBUTE);
				List<String> businessNamesRegistered = Arrays.asList(businessNames.split(FunctionConst.BUSINESS_ATTRIBUTE_SEPARATOR));
				if (!businessNamesRegistered.contains(businessName)) {
					throw new UnauthorizeExeption(new Error(FunctionConst.UNAUTHORIZED, "User " + username + " Unauthorized for business " + businessName), mapper);
				}
				
				LOGGER.info("INTRALE: username on token:" + username);
				
				// Validando si el usuario pertenece al grupo que tiene permitido ejecutar esta accion
				List groups = getGroups(authClaimsSet);
				if  ((!StringUtils.isEmpty(getFunctionGroup())) &&
						((groups==null) || (!groups.contains(getFunctionGroup())))){
					throw new UnauthorizeExeption(new Error(FunctionConst.UNAUTHORIZED, FunctionConst.UNAUTHORIZED), mapper);
				}
				
			} else {
				throw new UnauthorizeExeption(new Error(FunctionConst.NOT_AUTHORIZATION_FOUND, FunctionConst.NOT_AUTHORIZATION_FOUND), mapper);
			}
			
		}
		LOGGER.info("INTRALE: fin validate");
	}

	protected List getGroups(JWTClaimsSet claimsSet) {
		LOGGER.info("INTRALE: inicio getGroups");
		Map<String, Object> claims = claimsSet.getClaims();
		
		List groups = null;
		if (claims!=null && claims.size()>0) {
		 groups = (List) claimsSet.getClaims().get(FunctionConst.COGNITO_GROUPS);
		}
		LOGGER.info("INTRALE: fin getGroups");
		return groups;
	}

	private JWTClaimsSet validateToken(String token, String tokenUse)
			throws FunctionException {
		LOGGER.info("INTRALE: inicio validateToken");
		if (StringUtils.isEmpty(token)) {
			throw new TokenNotFoundException(new Error(FunctionConst.TOKEN_NOT_FOUND, FunctionConst.TOKEN_NOT_FOUND), mapper);
		}
		
		String jwt = token;
		if (token.contains(FunctionConst.BEARER)) {
			jwt = token.substring(FunctionConst.BEARER.length());
		}
		
		JWTClaimsSet claimsSet = null;
		try {
			claimsSet = processor.process(jwt, null);
		} catch (BadJWTException e) {
			if (e.getMessage().contains(FunctionConst.EXPIRED)) {
				LOGGER.info("INTRALE: token expired");
				throw new UnauthorizeExeption(new Error(FunctionConst.TOKEN_EXPIRED, FunctionConst.TOKEN_EXPIRED), mapper);
			}
			LOGGER.info("INTRALE: bad token");
			throw new BadRequestException(new Error(FunctionConst.BAD_TOKEN, FunctionConst.BAD_TOKEN), mapper);
		} catch (Exception e) {
			LOGGER.error("INTRALE: unexpected exception");
			LOGGER.error("INTRALE: authorization:" + token);
			LOGGER.error(FunctionException.toString(e));
			throw new UnexpectedException(new Error(FunctionConst.UNEXPECTED_EXCEPTION, FunctionConst.UNEXPECTED_EXCEPTION), mapper);
		} 
		
		if ((!isCorrectUserPool(claimsSet)) || 
				(!isCorrectTokenUse(claimsSet, tokenUse))) {
			LOGGER.info("INTRALE: invalid token");
			throw new UnauthorizeExeption(new Error(FunctionConst.INVALID_TOKEN, FunctionConst.INVALID_TOKEN), mapper);
		}
		
		LOGGER.info("INTRALE: fin validateToken");
		
		return claimsSet;
	}
	
	private boolean isCorrectUserPool(JWTClaimsSet claimsSet) {
       return userPoolIdUrl.contains(claimsSet.getIssuer());
	}
	 
	private boolean isCorrectTokenUse(JWTClaimsSet claimsSet, String tokenUseType) {
	       return claimsSet.getClaim(FunctionConst.TOKEN_USE).equals(tokenUseType);
	}

	
	public Validator getValidator() {
		return validator;
	}

	public PROV getProvider() {
		return provider;
	}

	public void setProvider(PROV provider) {
		this.provider = provider;
	}
	
   	public Class<Request> getProviderType() {
		return providerType;
	}
	
	public ApplicationConfig getConfig() {
		return config;
	}

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
	
}