package ar.com.intrale.cloud;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;

@Introspected
public class Lambda extends MicronautRequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Lambda.class);
	
	protected BaseFunction function;
   	
	@Inject
	private FunctionBuilder builder;
	
	@PostConstruct
	public void postConstruct() {
		LOGGER.debug("postConstruct");
	}
	
	@Override
    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent request) {
		LOGGER.info("Ejecutando lambda");
		
		function = builder.getfunction(request.getHeaders());
		
    	return function.lambdaApply(request.getHeaders(), request.getPathParameters(), request.getBody());
    }  

	public BaseFunction getFunction() {
		return function;
	}

	
}
