package ar.com.intrale.cloud;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.intrale.cloud.config.ApplicationConfig;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.scheduling.annotation.Scheduled;

@Controller("/")
@Requires(property = "app.microservices", value = "true", defaultValue = "false")
public class MicroService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MicroService.class);
	
	private IntraleFunction function;
	
	@Inject
	private ApplicationContext applicationContext;
	
	@Inject
	private ApplicationConfig config;
	
	private Long lastExecution = System.currentTimeMillis();
	
	public MicroService () {
		LOGGER.debug("Creando MicroService");
	}
	
	@Post(produces = MediaType.APPLICATION_JSON)
	public HttpResponse<String> post ( 
			@Header(Lambda.HEADER_AUTHORIZATION) String authorization, 
			@Header(Lambda.HEADER_BUSINESS_NAME) String businessName, 
			@Header(Lambda.HEADER_FUNCTION) String functionName, 
			@Body String request) {
		lastExecution = System.currentTimeMillis();
		
		//Instanciar Function
		Map <String, String> headers = new HashMap<String, String>();
		headers.put(Lambda.HEADER_AUTHORIZATION, authorization);
		headers.put(Lambda.HEADER_BUSINESS_NAME, businessName);
		headers.put(Lambda.HEADER_FUNCTION, functionName);
		
		LOGGER.info("INTRALE: functionName => " + functionName);
		LOGGER.info("INTRALE: authorization => " + authorization);
		LOGGER.info("INTRALE: businessName => " + businessName);
		if (!StringUtils.isEmpty(functionName)) {
			function = applicationContext.getBean(IntraleFunction.class, Qualifiers.byName(functionName.toUpperCase()));
		} else {
			function = applicationContext.getBean(IntraleFunction.class);
		}
		
		return (HttpResponse<String>) function.apply(headers, request);
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
