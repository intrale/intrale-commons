package ar.com.intrale;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.exceptions.BadRequestException;
import ar.com.intrale.exceptions.EmptyRequestException;
import ar.com.intrale.exceptions.FunctionException;
import ar.com.intrale.exceptions.UnexpectedException;
import ar.com.intrale.messages.RequestRoot;
import ar.com.intrale.messages.Error;
import io.micronaut.core.util.StringUtils;
import io.micronaut.validation.validator.Validator;

public class StringToRequestBuilder<REQ extends RequestRoot> implements Builder<String, REQ> {
	
	public static final String EMPTY_REQUEST = "EMPTY_REQUEST";
	
	public static final String UNEXPECTED = "UNEXPECTED";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StringToRequestBuilder.class);
	
	protected final Class<RequestRoot> requestType = (Class<RequestRoot>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	
   	@Inject
   	protected ObjectMapper mapper;
   	
	@Inject
   	protected Validator validator;

	@Override
	public REQ build(Map <String, String> headers, Map <String, String> queryStringParameters, String source) throws FunctionException {
		if (StringUtils.isEmpty(source)) {
    		throw new EmptyRequestException(new Error(EMPTY_REQUEST, EMPTY_REQUEST), mapper);
    	}
    	
		REQ requestObject = null;
		try {
			requestObject = (REQ) mapper.readValue(Base64.getDecoder().decode(source), requestType);
		} catch (Exception e) {
			LOGGER.error("Ocurrio un error parseando JSON:" + FunctionException.toString(e));
			throw new UnexpectedException(new Error(UNEXPECTED, e.getMessage()), mapper);
		}
		
    	validateRequestBuilded(requestObject);

    	requestObject.setHeaders(headers);
    	return requestObject;
	}

	protected void validateRequestBuilded(RequestRoot requestObject) throws BadRequestException {
		Collection<Error> errors = new ArrayList<Error>();
    	Set<ConstraintViolation<RequestRoot>> validatorErrors = validator.validate(requestObject, Default.class);
    	if (validatorErrors.size()>0) {
	    	Iterator<ConstraintViolation<RequestRoot>> it = validatorErrors.iterator();
	    	while (it.hasNext()) {
				ConstraintViolation<RequestRoot> constraintViolation = (ConstraintViolation<RequestRoot>) it.next();
				errors.add(new Error(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
			}
	    	LOGGER.info("retornando BadRequestException");
	    	throw new BadRequestException(errors, mapper);
    	}
	}



}
