package ar.com.intrale.cloud;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.exceptions.NonUniqueBeanException;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.StringUtils;
import io.micronaut.function.aws.MicronautRequestHandler;
import io.micronaut.http.HttpResponse;
import io.micronaut.inject.qualifiers.Qualifiers;

@Introspected
public class Lambda extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
	
	public static final String HEADER_FUNCTION = "function";
	public static final String HEADER_AUTHORIZATION = "Authorization";

	public static final String ALL = "*";

	public static final String GET_OPTIONS_HEAD_PUT_POST = "GET, OPTIONS, HEAD, PUT, POST";

	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	private static final Logger LOGGER = LoggerFactory.getLogger(Lambda.class);
	
	protected Function function;
	
   	@Inject
   	protected ApplicationContext applicationContext;
	
	@PostConstruct
	public void postConstruct() {
		LOGGER.debug("postConstruct");
	}
	
	@Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent request) {
		//Instanciar Function
		Map <String, String> headers = request.getHeaders();
		String functionName = StringUtils.EMPTY_STRING;
		String authorization = StringUtils.EMPTY_STRING;
		if (headers!=null) {
			functionName = headers.get(HEADER_FUNCTION); 
			authorization = headers.get(HEADER_AUTHORIZATION); 
		}
		if (!StringUtils.isEmpty(functionName)) {
			function = applicationContext.getBean(Function.class, Qualifiers.byName(functionName.toUpperCase()));
		} else {
			try {
				function = applicationContext.getBean(Function.class);
			} catch (NonUniqueBeanException e) {
				// En caso de que no se haya definido una funcion en el header y existan mas de una funcion candidata para ser instanciada
				// se intentara instanciar por default el READ
				function = applicationContext.getBean(Function.class, Qualifiers.byName(Function.READ));
			}
		}
		
 
    	HttpResponse<String> response =  (HttpResponse<String>) function.apply(request.getBody());
    	
    	APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
    	
		// CORS avaiable
		Map<String, String> responseHeaders = new HashMap<String, String>();
		responseHeaders.put(ACCESS_CONTROL_ALLOW_ORIGIN, ALL);
		responseHeaders.put(ACCESS_CONTROL_ALLOW_METHODS, GET_OPTIONS_HEAD_PUT_POST);
		responseEvent.setHeaders(responseHeaders); 
		
    	responseEvent.setStatusCode(response.getStatus().getCode());
    	responseEvent.setBody(response.body());
    	
    	return responseEvent;
    }  

	public Function getFunction() {
		return function;
	}

	
}
