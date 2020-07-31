package ar.com.intrale.cloud;

import java.util.Collection;

import com.fasterxml.jackson.databind.ObjectMapper;

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
