package ar.com.intrale.annotations;

import java.util.Date;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import ar.com.intrale.in.IntraleFunction;

@Aspect
@Component
public class IOLoggerAspect {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IOLoggerAspect.class);

	@Around("execution(@ ar.com.intrale.annotations.IOLogger * *(..)) && @annotation(ioLogger)")
	public Object execute(ProceedingJoinPoint joinPoint, IOLogger ioLogger) throws Throwable {
		Date start = new Date();
		String uuid = UUID.randomUUID().toString();
		
		LOGGER.info("**************************************************************************************************" );
		LOGGER.info("** Starting");
		LOGGER.info("** Executing ID:" + uuid );
		LOGGER.info("** Executing Signature:" + joinPoint.getSignature().toLongString() );
		LOGGER.info("** Start time:" + new Date() );
		LOGGER.info("** class:" + joinPoint.getTarget().getClass() );
		
		if (IntraleFunction.class.isInstance(joinPoint.getTarget())) {
			String authorization = (String) joinPoint.getArgs()[0];
			String request = (String) joinPoint.getArgs()[0];
			
			LOGGER.info("** authorization:" + authorization );
			LOGGER.info("** request:\n" + request );
		}
		LOGGER.info("**************************************************************************************************" );
		
		Object result = joinPoint.proceed();
		
		LOGGER.info("**************************************************************************************************" );
		LOGGER.info("** Ending");
		LOGGER.info("** Executing ID:" + uuid );
		LOGGER.info("** Executing Signature:" + joinPoint.getSignature().toLongString() );
		
		ResponseEntity<String> responseEntity = (ResponseEntity<String>) result;
		
		if (IntraleFunction.class.isInstance(joinPoint.getTarget())) {
			LOGGER.info("** httpStatusCode:" + responseEntity.getStatusCodeValue() );
			LOGGER.info("** response:\n" + responseEntity.getBody() );
		}

		LOGGER.info("** Execution time:" + (new Date().getTime() - start.getTime()) );
		LOGGER.info("**************************************************************************************************" );
		
		return result;
	}
}
