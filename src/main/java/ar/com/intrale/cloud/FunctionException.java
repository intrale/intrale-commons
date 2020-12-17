package ar.com.intrale.cloud;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;

public abstract class FunctionException extends java.lang.Exception {

	private Collection<Error> errors = new ArrayList<Error>();
	
   	protected ObjectMapper objectMapper;
	
	public FunctionException(Error error, ObjectMapper objectMapper){
		this.objectMapper = objectMapper;
		this.errors.add(error);
	}
	
	public FunctionException(Collection<Error> errors, ObjectMapper objectMapper){
		this.objectMapper = objectMapper;
		this.errors.addAll(errors);
	}
	
	protected  abstract HttpStatus getHttpStatus();
	

	public HttpResponse<String> getResponse(){
		try {
			FunctionExceptionResponse response = new FunctionExceptionResponse(errors);
			response.setStatusCode(getHttpStatus().getCode());
			return HttpResponseFactory.INSTANCE.status(getHttpStatus()).body(objectMapper.writeValueAsString(response));
		} catch (JsonProcessingException e) {
			return HttpResponseFactory.INSTANCE.status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
}
