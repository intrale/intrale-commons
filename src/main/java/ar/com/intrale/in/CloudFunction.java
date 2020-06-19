package ar.com.intrale.in;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

@Component
public class CloudFunction implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CloudFunction.class);

	@Autowired @Qualifier(IntraleFunction.NAME)
	private IntraleFunction function;

	public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent request) {
		
		LOGGER.debug("Atendiendo peticion: CloudFunction");
		
		String authorization = request.getHeaders().get(Authorizer.AUTHORIZATION);
		
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		
		// CORS avaiable
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Access-Control-Allow-Origin", "*");
		headers.put("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
		response.setHeaders(headers); 

		ResponseEntity<String> responseEntity = function.execute(authorization, request.getBody()); 
		response.setStatusCode(responseEntity.getStatusCodeValue());
		response.setBody(responseEntity.getBody());
		
		LOGGER.debug("Fin atencion de peticion: CloudFunction");
		return response;
	}
	
	
}
