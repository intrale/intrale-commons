package ar.com.intrale;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.intrale.config.ApplicationConfig;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.annotation.Scheduled;

@Controller("/")
@Requires(property = "app.microservices", value = "true", defaultValue = "false")
public class MicroService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MicroService.class);
	
	private BaseFunction function;
	
	@Inject
	private ApplicationContext applicationContext;
	
	@Inject
	private ApplicationConfig config;
	
	@Inject
	private FunctionBuilder builder;
	
	private Long lastExecution = System.currentTimeMillis();
	
	public MicroService () {
		LOGGER.debug("Creando MicroService");
	}
	
	@Post(produces = MediaType.APPLICATION_JSON)
	public Object post ( 
			@Header(FunctionBuilder.HEADER_AUTHORIZATION) String authorization, 
			@Header(FunctionBuilder.HEADER_BUSINESS_NAME) String businessName, 
			@Header(FunctionBuilder.HEADER_FUNCTION) String functionName, 
			@Body String request) {
		lastExecution = System.currentTimeMillis();
		
		//Instanciar Function
		Map <String, String> headers = new HashMap<String, String>();
		headers.put(FunctionBuilder.HEADER_AUTHORIZATION, authorization);
		headers.put(FunctionBuilder.HEADER_BUSINESS_NAME, businessName);
		headers.put(FunctionBuilder.HEADER_FUNCTION, functionName);
		
		function = builder.getfunction(headers);
		
		return function.msApply(headers, null, request);
	}
	
	@Scheduled(fixedDelay = "${app.activity.fixedDelay:'30s'}", initialDelay = "${app.activity.initialDelay:'15s'}")
	public void activityValidate() {
		LOGGER.debug("ejecutando activityValidate");
		Long actualInactivity = System.currentTimeMillis() - lastExecution;
		LOGGER.debug("Actual inactivity:" + actualInactivity + ", maxInactivity:" + config.getActivity().getMaxInactivity());
		if (config.getActivity().getEnabled() && (config.getActivity().getMaxInactivity() < actualInactivity)) {
			applicationContext.stop();
			System.exit(1);
		}
	}
	
}
