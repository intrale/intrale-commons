package ar.com.intrale.cloud;

import java.util.Map;

import javax.inject.Singleton;

import ar.com.intrale.cloud.exceptions.FunctionException;

@Singleton
public class ObjectToSameBuilder implements BuilderForLambda<Object, Object> {

	@Override
	public Object build(Map<String, String> headers, Object source) throws FunctionException {
		return source;
	}

	@Override
	public Object wrapForLambda(Object target) {
		return target;
	}

}
