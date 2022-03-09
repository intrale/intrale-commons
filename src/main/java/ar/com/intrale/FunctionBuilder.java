package ar.com.intrale;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.exceptions.NonUniqueBeanException;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.qualifiers.Qualifiers;

@Singleton
public class FunctionBuilder {
	
	public static final String HEADER_FUNCTION = "function";
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_ID_TOKEN = "idtoken";
	public static final String HEADER_BUSINESS_NAME = "businessname";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";

	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionBuilder.class);
	
   	@Inject
   	protected ApplicationContext applicationContext;
	
	public BaseFunction getfunction (Map <String, String> headers) {
		BaseFunction function;
		String functionName = StringUtils.EMPTY_STRING;

		if (headers!=null) {
			functionName = headers.get(HEADER_FUNCTION); 

			LOGGER.info("INTRALE: " + HEADER_FUNCTION + " => " + headers.get(HEADER_FUNCTION));
			LOGGER.info("INTRALE: " + HEADER_AUTHORIZATION + " => " + headers.get(HEADER_AUTHORIZATION));
			LOGGER.info("INTRALE: " + HEADER_ID_TOKEN + " => " + headers.get(HEADER_ID_TOKEN));
			LOGGER.info("INTRALE: " + HEADER_BUSINESS_NAME + " => " + headers.get(HEADER_BUSINESS_NAME));
			LOGGER.info("INTRALE: " + HEADER_CONTENT_TYPE + " => " + headers.get(HEADER_CONTENT_TYPE));
		}
		
		if (!StringUtils.isEmpty(functionName)) {
			function = applicationContext.getBean(BaseFunction.class, Qualifiers.byName(functionName.toUpperCase()));
		} else {
			try {
				function = applicationContext.getBean(BaseFunction.class);
			} catch (NonUniqueBeanException e) {
				// En caso de que no se haya definido una funcion en el header y existan mas de una funcion candidata para ser instanciada
				// se intentara instanciar por default el READ
				function = applicationContext.getBean(BaseFunction.class, Qualifiers.byName(FunctionConst.READ));
			}
		}
		
		return function;
	}
}
