package ar.com.intrale.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.core.util.StringUtils;
import io.micronaut.validation.validator.Validator;

@Singleton
public class RequestBuilder {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestBuilder.class);

   	@Inject
   	protected ObjectMapper objectMapper;

   	@Inject
   	protected Validator validator;
   	
	public Request build(String request, Class<Request> requestType) throws FunctionException {  	
		if (StringUtils.isEmpty(request)) {
    		throw new EmptyRequestException(new Error("EMPTY_REQUEST", "EMPTY_REQUEST"), objectMapper);
    	}
    	
		Request requestObject = null;
		try {
			requestObject = (Request) objectMapper.readValue(request, requestType);
		} catch (JsonProcessingException e) {
			throw new UnexpectedException(new Error("UNEXPECTED", e.getMessage()), objectMapper);
		}
		
    	Collection<Error> errors = new ArrayList<Error>();
    	
    	Set<ConstraintViolation<Request>> validatorErrors = validator.validate(requestObject, Default.class);
    	if (validatorErrors.size()>0) {
	    	Iterator<ConstraintViolation<Request>> it = validatorErrors.iterator();
	    	while (it.hasNext()) {
				ConstraintViolation<ar.com.intrale.cloud.Request> constraintViolation = (ConstraintViolation<ar.com.intrale.cloud.Request>) it.next();
				errors.add(new Error(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
			}
	    	LOGGER.info("retornando error");
	    	throw new BadRequestException(errors, objectMapper);
    	}

    	return requestObject;
	}
}
