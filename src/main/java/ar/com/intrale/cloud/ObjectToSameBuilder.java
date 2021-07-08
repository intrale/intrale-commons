package ar.com.intrale.cloud;

import java.util.Map;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.intrale.cloud.exceptions.FunctionException;

@Singleton
public class ObjectToSameBuilder implements BuilderForLambda<Object, Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectToSameBuilder.class);
	
	@Override
	public Object build(Map<String, String> headers, Object source) throws FunctionException {
		LOGGER.info("ObjectToSameBuilder build");
		return source;
	}

	@Override
	public Object wrapForLambda(Object target) {
		LOGGER.info("ObjectToSameBuilder wrapForLambda");
		return target;
	}

}
