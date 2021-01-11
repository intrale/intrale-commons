package ar.com.intrale.cloud;

import java.util.Optional;

import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;

public class MicroServiceCondition implements Condition{

	@Override
	public boolean matches(ConditionContext context) {
		Optional<String> microservices = context.getProperty("micronaut.application.microservices", String.class);
		
		System.out.println("microservices:" + microservices.get());
		Boolean microservicesValue =  Boolean.valueOf(microservices.get());
		System.out.println("microservicesValue:" + microservicesValue);
		return microservicesValue;
	}

}
