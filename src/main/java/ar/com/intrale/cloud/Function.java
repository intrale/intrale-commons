package ar.com.intrale.cloud;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.validation.validator.Validator;


public abstract class Function<REQ extends Request, RES extends Response> implements java.util.function.Function<String, HttpResponse<String>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Function.class);
	
	private final Class<Request> requestType = (Class<Request>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	
   	@Inject
   	protected Validator validator;
	
   	@Inject
   	protected ObjectMapper objectMapper;
   	
   	/*@Inject
   	protected RequestBuilder builder;*/
   	
	@Override
	public HttpResponse<String> apply(String request) {
		
    	LOGGER.info("INTRALE: iniciando funcion");
    	LOGGER.info("INTRALE: request => \n" + request);
    	try {
    		REQ requestObject = (REQ) build(request, requestType);
	    	
	    	if (StringUtils.isEmpty(request)) {
	    		return HttpResponse.badRequest();
	    	}
	    	
	    	RES res = execute(requestObject);
	    	res.setStatusCode(200);
	    	
	    	return logResponse(HttpResponse.ok().body(objectMapper.writeValueAsString(res)));
		} catch (FunctionException e) {
			return logResponse(e.getResponse());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return logResponse(HttpResponse.serverError());
        
	}

	
	private HttpResponse<String> logResponse(HttpResponse<String> response) {
		LOGGER.info("INTRALE: response => \n" + response.body());
		return response;
	}
	
	public abstract RES execute (REQ request) throws FunctionException;

	
	protected Request build(String request, Class<Request> requestType) throws FunctionException {  	
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
