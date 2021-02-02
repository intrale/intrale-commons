package ar.com.intrale.cloud;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import io.micronaut.http.HttpResponse;

@Introspected
public class Lambda extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
	
	public static final String ALL = "*";

	public static final String GET_OPTIONS_HEAD_PUT_POST = "GET, OPTIONS, HEAD, PUT, POST";

	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";

	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	private static final Logger LOGGER = LoggerFactory.getLogger(Lambda.class);
	
	@Inject
	protected Function function;
	
	@PostConstruct
	public void postConstruct() {
		LOGGER.debug("postConstruct");
	}
	
	@Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent request) {
 
    	HttpResponse<String> response =  (HttpResponse<String>) function.apply(request.getBody());
    	
    	APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
    	
		// CORS avaiable
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(ACCESS_CONTROL_ALLOW_ORIGIN, ALL);
		headers.put(ACCESS_CONTROL_ALLOW_METHODS, GET_OPTIONS_HEAD_PUT_POST);
		responseEvent.setHeaders(headers); 
		
    	responseEvent.setStatusCode(response.getStatus().getCode());
    	responseEvent.setBody(response.body());
    	
    	return responseEvent;
    }  

	public Function getFunction() {
		return function;
	}

	
}
