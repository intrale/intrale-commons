package ar.com.intrale.in;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(
	    value="controller.enabled", 
	    havingValue = "true", 
	    matchIfMissing = true)
public class CloudController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CloudController.class);
	
	private Date lastExecute = new Date();
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired @Qualifier(IntraleFunction.NAME)
	private IntraleFunction function;
	
	@RequestMapping(value="/", produces = MediaType.APPLICATION_JSON_VALUE)
	//@ResponseBody
	public ResponseEntity<String> apply(@RequestHeader(required = false, name = Authorizer.AUTHORIZATION) String authorization, @RequestBody String body) {
		LOGGER.debug("Atendiendo peticion: CloudController");
		lastExecute = new Date();
		ResponseEntity response = function.execute(authorization, body);
		LOGGER.debug("Fin atencion de peticion: CloudController");
		return response;
	}

	@Configuration
	@ConditionalOnProperty(
		    value="controller.enabled", 
		    havingValue = "true", 
		    matchIfMissing = true)
	@EnableScheduling
	public class SchedulerConfiguration {

		@Value("${controller.validation.maxInactivity}")
		private Long maxInactivity;

		@Scheduled(fixedRateString = "${controller.validation.period}")
		public void execute() {
			System.out.println("Validando actividad ...");
			if ((new Date().getTime() - lastExecute.getTime())>maxInactivity) {
				int exitCode = SpringApplication.exit(context, new ExitCodeGenerator() {
						public int getExitCode() {
					        // return the error code
					        return 0;
					    }
					});
			}
		}
	}
	
	
}
