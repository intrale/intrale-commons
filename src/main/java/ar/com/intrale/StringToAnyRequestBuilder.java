package ar.com.intrale;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.intrale.exceptions.BadRequestException;
import ar.com.intrale.exceptions.FunctionException;
import ar.com.intrale.messages.RequestRoot;

public class StringToAnyRequestBuilder<MAIN extends RequestRoot, SEC extends RequestRoot, THIRD extends RequestRoot> extends StringToRequestBuilder<MAIN> {

	protected final Class<RequestRoot> secRequestType = (Class<RequestRoot>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	protected final Class<RequestRoot> thirdRequestType = (Class<RequestRoot>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[2];
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StringToAnyRequestBuilder.class);
	
	@Override
	public MAIN build(Map<String, String> headers, Map<String, String> queryStringParameters, String source)
			throws FunctionException {
		try {
			LOGGER.info("StringToAnyRequestBuilder1:" + requestType.getCanonicalName());
			return super.build(headers, queryStringParameters, source);
		} catch (BadRequestException e1) {
			try {
				LOGGER.info("StringToAnyRequestBuilder2:" + secRequestType.getCanonicalName());
				return super.buildWithRequestType(headers, source, secRequestType);
			} catch (BadRequestException e2) {
				LOGGER.info("StringToAnyRequestBuilder3:" + thirdRequestType.getCanonicalName());
					return super.buildWithRequestType(headers, source, thirdRequestType);
			}
		}
	}

}
