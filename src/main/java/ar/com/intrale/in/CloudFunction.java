package ar.com.intrale.in;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

@Component
public class CloudFunction implements Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>{

	@Autowired @Qualifier(IntraleFunction.NAME)
	private IntraleFunction function;

	public APIGatewayProxyResponseEvent apply(APIGatewayProxyRequestEvent request) {
		
		String authorization = request.getHeaders().get(Authorizer.AUTHORIZATION);
		
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		
		response.setStatusCode(200);
		
		// CORS avaiable
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Access-Control-Allow-Origin", "*");
		headers.put("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
		response.setHeaders(headers); 

		response.setBody(function.execute(authorization, request.getBody()));
		
		return response;
	}
	
	
}
