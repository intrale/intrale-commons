package ar.com.intrale.cloud;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.ClientContext;
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
		Map <String, String> headers = new HashMap<String, String>();
		try {
			ClientContext clientContext = applicationContext.getBean(ClientContext.class);
			LOGGER.info(" clientContext.getCustom():" + clientContext.getCustom());
			LOGGER.info(" clientContext.getEnvironment()):" + clientContext.getEnvironment());
		} catch (Exception e) {
			LOGGER.info("No fue posible instanciar ClientContext");
		}
		
		LOGGER.info("Tipo recibido:" + request.getClass());
		
		//Instanciar Function
		if (request instanceof APIGatewayProxyRequestEvent) {
			APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent = (APIGatewayProxyRequestEvent) request;
			headers.putAll(apiGatewayProxyRequestEvent.getHeaders());
		}
		if (request instanceof LinkedHashMap) {
			LOGGER.info("Contenido:" + ((LinkedHashMap)request).toString());
		}
		
		function = builder.getfunction(headers);
 
    	return function.lambdaApply(headers, request);
    }  

	public BaseFunction getFunction() {
		return function;
	}

	
}
