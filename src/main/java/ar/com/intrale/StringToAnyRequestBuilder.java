package ar.com.intrale;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import ar.com.intrale.exceptions.BadRequestException;
import ar.com.intrale.exceptions.FunctionException;
import ar.com.intrale.messages.RequestRoot;

public class StringToAnyRequestBuilder<MAIN extends RequestRoot, SEC extends RequestRoot> extends StringToRequestBuilder<MAIN> {

	protected final Class<RequestRoot> secRequestType = (Class<RequestRoot>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	
	@Override
	public MAIN build(Map<String, String> headers, Map<String, String> queryStringParameters, String source)
			throws FunctionException {
		try {
			return super.build(headers, queryStringParameters, source);
		} catch (BadRequestException e) {
			return super.buildWithRequestType(headers, source, secRequestType);
		}
	}

}
