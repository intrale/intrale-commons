package ar.com.intrale.tools;

import java.awt.List;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.RequestRoot;
import ar.com.intrale.exceptions.FunctionException;
import io.micronaut.core.util.StringUtils;

public abstract class JsonConverter<E> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);
	
	protected final Class converterType = (Class<RequestRoot>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	
   	protected ObjectMapper mapper = new ObjectMapper();

    public String convert(final E entity) {
        try {
        	LOGGER.info("convert:" + converterType.getName());
        	if (mapper==null) {
        		LOGGER.info("ObjectMapper es nulo");
        	}
			return mapper.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			LOGGER.error(FunctionException.toString(e));
			return StringUtils.EMPTY_STRING;
		}
    }

    public E unconvert(final String string) {
    	try {
    		LOGGER.info("unconvert:" + converterType.getName());
			return mapper.readValue(string, mapper.getTypeFactory().constructCollectionType(Collection.class, converterType));
		} catch (Exception e) {
			LOGGER.error(FunctionException.toString(e));
			return null;
		}
    }
	
	
}
