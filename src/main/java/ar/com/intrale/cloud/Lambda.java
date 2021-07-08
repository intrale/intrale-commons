package ar.com.intrale.cloud;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;

@Introspected
public class Lambda extends MicronautRequestHandler<APIGatewayProxyRequestEvent, Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(Lambda.class);
	
	protected BaseFunction function;
   	
	@Inject
	private FunctionBuilder builder;
	
	@PostConstruct
	public void postConstruct() {
		LOGGER.debug("postConstruct");
	}
	
	@Override
    public Object execute(APIGatewayProxyRequestEvent request) {
		//Instanciar Function
		Map <String, String> headers = request.getHeaders();
		
		function = builder.getfunction(request.getHeaders());
 
    	return function.lambdaApply(headers, request);
    }  

	public BaseFunction getFunction() {
		return function;
	}

	
}
