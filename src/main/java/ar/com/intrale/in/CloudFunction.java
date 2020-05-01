package ar.com.intrale.in;

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
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(200);

		response.setBody(function.execute(request.getBody()));
		
		return response;
	}
	
	
}
