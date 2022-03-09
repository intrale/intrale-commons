package ar.com.intrale.exceptions;

import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;

import ar.com.intrale.Error;
import io.micronaut.http.HttpStatus;

public class BadRequestException extends FunctionException {

	public BadRequestException(Error error, ObjectMapper objectMapper) {
		super(error, objectMapper);
	}

	public BadRequestException(Collection<Error> errors, ObjectMapper objectMapper) {
		super(errors, objectMapper);
	}

	@Override
	protected HttpStatus getHttpStatus() {
		return HttpStatus.BAD_REQUEST;
	}

}
