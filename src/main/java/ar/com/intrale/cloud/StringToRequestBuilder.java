package ar.com.intrale.cloud;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.cloud.exceptions.BadRequestException;
import ar.com.intrale.cloud.exceptions.EmptyRequestException;
import ar.com.intrale.cloud.exceptions.FunctionException;
import ar.com.intrale.cloud.exceptions.UnexpectedException;
import io.micronaut.core.util.StringUtils;
import io.micronaut.validation.validator.Validator;

public class StringToRequestBuilder<REQ extends Request> implements Builder<String, REQ> {
	
	public static final String EMPTY_REQUEST = "EMPTY_REQUEST";
	
	public static final String UNEXPECTED = "UNEXPECTED";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StringToRequestBuilder.class);
	
	protected final Class<Request> requestType = (Class<Request>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	
   	@Inject
   	protected ObjectMapper mapper;
   	
	@Inject
   	protected Validator validator;

	@Override
	public REQ build(Map <String, String> headers, String source) throws FunctionException {
		if (StringUtils.isEmpty(source)) {
    		throw new EmptyRequestException(new Error(EMPTY_REQUEST, EMPTY_REQUEST), mapper);
    	}
    	
		REQ requestObject = null;
		try {
			requestObject = (REQ) mapper.readValue(source, requestType);
		} catch (JsonProcessingException e) {
			throw new UnexpectedException(new Error(UNEXPECTED, e.getMessage()), mapper);
		}
		
    	validateRequestBuilded(requestObject);

    	requestObject.setHeaders(headers);
    	return requestObject;
	}

	protected void validateRequestBuilded(Request requestObject) throws BadRequestException {
		Collection<Error> errors = new ArrayList<Error>();
    	Set<ConstraintViolation<Request>> validatorErrors = validator.validate(requestObject, Default.class);
    	if (validatorErrors.size()>0) {
	    	Iterator<ConstraintViolation<Request>> it = validatorErrors.iterator();
	    	while (it.hasNext()) {
				ConstraintViolation<ar.com.intrale.cloud.Request> constraintViolation = (ConstraintViolation<ar.com.intrale.cloud.Request>) it.next();
				errors.add(new Error(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
			}
	    	LOGGER.info("retornando error");
	    	throw new BadRequestException(errors, mapper);
    	}
	}



}
