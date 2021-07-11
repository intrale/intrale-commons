package ar.com.intrale.cloud;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;

@Introspected
public class Lambda extends MicronautRequestHandler<Object, Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Lambda.class);
	
	protected BaseFunction function;
   	
	@Inject
	private FunctionBuilder builder;
	
	@PostConstruct
	public void postConstruct() {
		LOGGER.debug("postConstruct");
	}
	
	@Override
    public Object execute(Object request) {
		
		LOGGER.info("Tipo recibido:" + request.getClass());
		
		//Instanciar Function
		Map <String, String> headers = new HashMap<String, String>();
		if (request instanceof APIGatewayProxyRequestEvent) {
			APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent = (APIGatewayProxyRequestEvent) request;
			headers.putAll(apiGatewayProxyRequestEvent.getHeaders());
		}
		
		function = builder.getfunction(headers);
 
    	return function.lambdaApply(headers, request);
    }  

	public BaseFunction getFunction() {
		return function;
	}

	
}
