package ar.com.intrale.cloud;

import java.util.function.Function;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.annotation.Scheduled;

@Controller("/")
@Requires( condition = MicroServiceCondition.class )
public class MicroService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MicroService.class);
	
	@Inject
	private Function function;
	
	@Inject
	private ApplicationContext context;
	
	@Value("${app.activity.enabled}")
	private Boolean activityValidateEnabled;
	
	@Value("${app.activity.maxInactivity}")
	private Long maxInactivity;
	
	private Long lastExecution = System.currentTimeMillis();
	
	public MicroService () {
		LOGGER.debug("Creando MicroService");
	}
	
	@Post()
	public HttpResponse<String> post (@Body String request) {
		lastExecution = System.currentTimeMillis();
		return (HttpResponse<String>) function.apply(request);
	}
	
	@Scheduled(fixedDelay = "${app.activity.fixedDelay}", initialDelay = "${app.activity.initialDelay}")
	public void activityValidate() {
		LOGGER.debug("ejecutando activityValidate");
		Long actualInactivity = System.currentTimeMillis() - lastExecution;
		LOGGER.debug("Actual inactivity:" + actualInactivity + ", maxInactivity:" + maxInactivity);
		if (activityValidateEnabled && (maxInactivity < actualInactivity)) {
			context.stop();
			System.exit(1);
		}
	}
	
}
