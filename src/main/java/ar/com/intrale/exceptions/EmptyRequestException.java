package ar.com.intrale.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.messages.Error;
import io.micronaut.http.HttpStatus;


public class EmptyRequestException extends FunctionException {

	public EmptyRequestException(Error error, ObjectMapper objectMapper) {
		super(error, objectMapper);
	}

	@Override
	protected HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}

}
