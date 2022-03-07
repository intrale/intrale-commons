package ar.com.intrale.cloud.tools;

import java.lang.reflect.ParameterizedType;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.cloud.RequestRoot;
import ar.com.intrale.cloud.exceptions.FunctionException;
import io.micronaut.core.util.StringUtils;

public abstract class JsonConverter<E> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);
	
	protected final Class converterType = (Class<RequestRoot>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	
   	@Inject
   	protected ObjectMapper mapper;

    public String convert(final E entity) {
        try {
			return mapper.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			LOGGER.error(FunctionException.toString(e));
			return StringUtils.EMPTY_STRING;
		}
    }

    public E unconvert(final String string) {
    	try {
			return (E) mapper.readValue(string, converterType);
		} catch (Exception e) {
			LOGGER.error(FunctionException.toString(e));
			return null;
		}
    }
	
	
}
