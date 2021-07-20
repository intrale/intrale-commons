package ar.com.intrale.cloud;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.cloud.exceptions.FunctionException;
import io.micronaut.http.HttpResponse;

@Singleton
public class FunctionResponseToHttpResponseBuilder implements BuilderForLambda<Response, HttpResponse<String>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionResponseToHttpResponseBuilder.class);
	
   	@Inject
   	protected ObjectMapper mapper;
	
	@Override
	public HttpResponse<String> build(Map<String, String> headers, Map <String, String> queryStringParameters, Response source) throws FunctionException {
		try {
			source.setStatusCode(200); //TODO: Revisar si es necesaria esta linea
			HttpResponse<String> response = HttpResponse.ok().body(mapper.writeValueAsString(source));
			LOGGER.info("INTRALE: response => \n" + response.body());
			return response;
		} catch (Exception e) {
			LOGGER.error(FunctionException.toString(e));
			return HttpResponse.serverError();
		}
	}

	@Override
	public APIGatewayProxyResponseEvent wrapForLambda(HttpResponse<String> target) {
    	APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
    	
		// CORS avaiable
		Map<String, String> responseHeaders = new HashMap<String, String>();
		responseHeaders.put(FunctionConst.ACCESS_CONTROL_ALLOW_ORIGIN, FunctionConst.ALL);
		responseHeaders.put(FunctionConst.ACCESS_CONTROL_ALLOW_METHODS, FunctionConst.GET_OPTIONS_HEAD_PUT_POST);
		responseHeaders.put(FunctionConst.ACCESS_CONTROL_ALLOW_HEADERS, FunctionConst.ALLOW_HEADERS_AVAIABLES);
		responseEvent.setHeaders(responseHeaders); 
		
    	responseEvent.setStatusCode(target.getStatus().getCode());
    	responseEvent.setBody((String) target.body());
    	responseEvent.setIsBase64Encoded(Boolean.TRUE);
    	
    	return responseEvent;
	}

}
