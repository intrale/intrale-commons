package ar.com.intrale;

import java.util.Map;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.intrale.exceptions.FunctionException;

@Singleton
public class ObjectToSameBuilder implements Builder<Object, Object> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectToSameBuilder.class);
	
	@Override
	public Object build(Map<String, String> headers, Map <String, String> queryStringParameters, Object source) throws FunctionException {
		LOGGER.info("ObjectToSameBuilder build");
		return source;
	}


}
