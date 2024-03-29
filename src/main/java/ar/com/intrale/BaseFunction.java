package ar.com.intrale;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import ar.com.intrale.config.ApplicationConfig;
import ar.com.intrale.exceptions.BadRequestException;
import ar.com.intrale.exceptions.BusinessNotFoundException;
import ar.com.intrale.exceptions.FunctionException;
import ar.com.intrale.exceptions.TokenNotFoundException;
import ar.com.intrale.exceptions.UnauthorizeExeption;
import ar.com.intrale.exceptions.UnexpectedException;
import ar.com.intrale.messages.RequestRoot;
import ar.com.intrale.messages.Response;
import ar.com.intrale.messages.Error;
import ar.com.intrale.messages.FunctionExceptionResponse;
import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.validation.validator.Validator;

/**
 * Funcion cuya entrada y salida esta en formato JSON
 * Importante mantener la relacion 1 a 1 entre funcion y provider utilizado
 * En caso de necesitar utilizar mas de un provider, separar la logica en dos funciones
 */
public abstract class BaseFunction<	FUNCTION_REQ extends RequestRoot, 
									FUNCTION_RES, 
									PROV, 
									REQ_BUILDER extends Builder,
									RES_BUILDER extends BuilderForLambda> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseFunction.class);

	protected final Class<RequestRoot> providerType = (Class<RequestRoot>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	protected final Class<RequestRoot> requestBuilderType = (Class<RequestRoot>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];
	protected final Class<RequestRoot> responseBuilderType = (Class<RequestRoot>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[4];
	
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
			LOGGER.info("INTRALE: No fue posible inicializar el provider:" + providerType + ", =>" + FunctionException.toString(e));
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
    }
	
	public abstract FUNCTION_RES execute (FUNCTION_REQ request) throws FunctionException;
	
	public Object msApply(Map <String, String> headers, Map <String, String> pathParameters, String request) {
		
    	LOGGER.info("INTRALE: iniciando funcion");
    	LOGGER.info("INTRALE: request => \n" + request);
    	LOGGER.info("INTRALE: headers => \n" + headers);
    	LOGGER.info("INTRALE: pathParameters => \n" + pathParameters);
    	
    	try {

			//LOGGER.info("INTRALE: Normalizando headers keys");
			//headers = normalizeHeaders(headers);

			LOGGER.info("INTRALE: Validando headers & parameters");
			validate(headers, pathParameters);
    		
    		LOGGER.info("INTRALE: Construyendo request");
    		
    		FUNCTION_REQ requestObject = (FUNCTION_REQ) buildRequest(headers, pathParameters, request);
    		
    		LOGGER.info("INTRALE: Ejecutando");
	    	
	    	FUNCTION_RES res = execute(requestObject);
	    	
	    	LOGGER.info("INTRALE: Construyendo respuesta");
	    	
	    	return buildResponse(res);
	    	
		} catch (FunctionException e) {
			LOGGER.error(FunctionException.toString(e));
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

	public static Map<String, String> normalizeHeaders(Map<String, String> headers) {
		return headers.keySet().stream().collect(Collectors.toMap(element -> element.toLowerCase(), element -> headers.get(element)));
	}

	public APIGatewayProxyResponseEvent lambdaApply(Map <String, String> headers, Map <String, String> pathParameters, String request) {
		Object response = msApply(headers, pathParameters, request);
		RES_BUILDER builder = (RES_BUILDER) applicationContext.getBean(responseBuilderType);
		return builder.wrapForLambda(response);
	}
	
	private Object buildResponse(FUNCTION_RES res) throws Exception {
		RES_BUILDER builder = (RES_BUILDER) applicationContext.getBean(responseBuilderType);
		return builder.build(null, null, res);
	}
	
	protected FUNCTION_REQ buildRequest(Map <String, String> headers, Map <String, String> pathParameters, Object request) throws FunctionException {  	
		REQ_BUILDER builder = (REQ_BUILDER) applicationContext.getBean(requestBuilderType);
		return (FUNCTION_REQ) builder.build(headers, pathParameters, request);
	}
	
	// Segurizacion de la funcion
	
	public void validate (Map <String, String> headers, Map <String, String> pathParameters) throws FunctionException {
		LOGGER.info("INTRALE: inicio validate");
		
		String authorization = headers.get(FunctionBuilder.HEADER_AUTHORIZATION);
		String idToken = headers.get(FunctionBuilder.HEADER_ID_TOKEN);
		
		LOGGER.info("INTRALE: authorization:" + authorization);
		LOGGER.info("INTRALE: idToken:" + idToken);
		
		
		
		LOGGER.info("INTRALE: validando permisos");
		
		if (isSecurityEnabled()) {
			if (authorization!=null){
				String businessName = getBusinessName(headers, pathParameters);
				
				JWTClaimsSet authClaimsSet = validateToken(authorization, FunctionConst.ACCESS);
				JWTClaimsSet idTokenClaimsSet = validateToken(idToken, FunctionConst.ID);
				
				//Se valida que el usuario este registrado para la organizacion / negocio que desea ejecutar la funcion
				String username = (String) authClaimsSet.getClaims().get(FunctionConst.USERNAME);
				String businessNames = (String) idTokenClaimsSet.getClaims().get(FunctionConst.BUSINESS_ATTRIBUTE);
				List<String> businessNamesRegistered = Arrays.asList(businessNames.split(FunctionConst.BUSINESS_ATTRIBUTE_SEPARATOR));
				if (!businessNamesRegistered.contains(businessName)) {
					LOGGER.info("INTRALE: " + "User " + username + " Unauthorized for business " + businessName);
					throw new UnauthorizeExeption(new Error(FunctionConst.UNAUTHORIZED, "User " + username + " Unauthorized for business " + businessName), mapper);
				}
				
				LOGGER.info("INTRALE: username on token:" + username);
				
				// Validando si el usuario pertenece al grupo que tiene permitido ejecutar esta accion
				List groups = getGroups(authClaimsSet);
				if  ((!StringUtils.isEmpty(getFunctionGroup())) &&
						((groups==null) || (!groups.contains(getFunctionGroup())))){
					LOGGER.info("INTRALE: " + "User " + username + " not belong for group " + getFunctionGroup());
					throw new UnauthorizeExeption(new Error(FunctionConst.UNAUTHORIZED, FunctionConst.UNAUTHORIZED), mapper);
				}
				
			} else {
				throw new UnauthorizeExeption(new Error(FunctionConst.NOT_AUTHORIZATION_FOUND, FunctionConst.NOT_AUTHORIZATION_FOUND), mapper);
			}
			
		}
		LOGGER.info("INTRALE: fin validate");
	}

	protected String getBusinessName(Map<String, String> headers, Map<String, String> pathParameters)
			throws BusinessNotFoundException {
		String businessName = headers.get(FunctionBuilder.HEADER_BUSINESS_NAME);
		if (StringUtils.isEmpty(businessName) && pathParameters!=null) {
			businessName = pathParameters.get(FunctionBuilder.HEADER_BUSINESS_NAME);
		}

		if (StringUtils.isEmpty(businessName)) {
			LOGGER.info("INTRALE: businessName not found");
			throw new BusinessNotFoundException(new Error(FunctionConst.BUSINESS_NOT_FOUND, FunctionConst.BUSINESS_NOT_FOUND), mapper);
		}
		return businessName;
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
	
   	public Class<RequestRoot> getProviderType() {
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
	
	/**
	 * Invoca a otra funcion
	 * @param functionClass
	 * @param callRequest
	 * @throws FunctionException
	 */
	protected Response callFunction(Class functionClass, RequestRoot callRequest, RequestRoot parentRequest) {
		try {
			callRequest.setRequestId(parentRequest.getRequestId());
			callRequest.setHeaders(parentRequest.getHeaders());
			callRequest.setPathParameters(parentRequest.getPathParameters());
			BaseFunction function = (BaseFunction) applicationContext.getBean(functionClass);
			Response response = (Response) function.execute(callRequest);
			return response;
		} catch (FunctionException e) {
			return new FunctionExceptionResponse(e.getErrors());
		}
	}
	
}
