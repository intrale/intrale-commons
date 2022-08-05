package ar.com.intrale.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.messages.Error;
import ar.com.intrale.messages.FunctionExceptionResponse;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpResponseFactory;
import io.micronaut.http.HttpStatus;

public abstract class FunctionException extends java.lang.Exception {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FunctionException.class);

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
	
   	public Collection<Error> getErrors() {
		return errors;
	}

	public HttpResponse<String> getResponse(){
		try {
			FunctionExceptionResponse response = new FunctionExceptionResponse(errors);
			response.setStatusCode(getHttpStatus().getCode());
			
			String exceptionResponse = objectMapper.writeValueAsString(response);
			
			LOGGER.info("exceptionResponse:" + exceptionResponse);
			
			return HttpResponseFactory.INSTANCE.status(getHttpStatus()).body(Base64.getEncoder().encodeToString(exceptionResponse.getBytes()));
		} catch (JsonProcessingException e) {
			LOGGER.error(FunctionException.toString(e));
			return HttpResponseFactory.INSTANCE.status(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	/**
	 * Retorna el stack de la exception en formato string
	 * @param e
	 * @return
	 */
	public static String toString(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		return stringWriter.toString();
	}
	
}
